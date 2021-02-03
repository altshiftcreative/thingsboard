package com.asc.bluewaves.lwm2m.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.leshan.core.attributes.AttributeSet;
import org.eclipse.leshan.core.model.LwM2mModel;
import org.eclipse.leshan.core.model.ObjectLoader;
import org.eclipse.leshan.core.model.ResourceModel;
import org.eclipse.leshan.core.model.ResourceModel.Type;
import org.eclipse.leshan.core.model.StaticModel;
import org.eclipse.leshan.core.node.LwM2mNode;
import org.eclipse.leshan.core.node.LwM2mObjectInstance;
import org.eclipse.leshan.core.node.LwM2mPath;
import org.eclipse.leshan.core.node.LwM2mSingleResource;
import org.eclipse.leshan.core.request.ContentFormat;
import org.eclipse.leshan.core.request.CreateRequest;
import org.eclipse.leshan.core.request.DeleteRequest;
import org.eclipse.leshan.core.request.DiscoverRequest;
import org.eclipse.leshan.core.request.ExecuteRequest;
import org.eclipse.leshan.core.request.ObserveRequest;
import org.eclipse.leshan.core.request.ReadRequest;
import org.eclipse.leshan.core.request.WriteAttributesRequest;
import org.eclipse.leshan.core.request.WriteRequest;
import org.eclipse.leshan.core.request.WriteRequest.Mode;
import org.eclipse.leshan.core.request.exception.InvalidRequestException;
import org.eclipse.leshan.core.response.CreateResponse;
import org.eclipse.leshan.core.response.DeleteResponse;
import org.eclipse.leshan.core.response.DiscoverResponse;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.LwM2mResponse;
import org.eclipse.leshan.core.response.ObserveResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteAttributesResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.eclipse.leshan.server.californium.LeshanServer;
import org.eclipse.leshan.server.registration.Registration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.asc.bluewaves.lwm2m.converter.MagicLwM2mValueConverter;
import com.asc.bluewaves.lwm2m.model.json.LwM2mNodeDeserializer;
import com.asc.bluewaves.lwm2m.model.json.LwM2mNodeSerializer;
import com.asc.bluewaves.lwm2m.model.json.RegistrationSerializer;
import com.asc.bluewaves.lwm2m.model.json.ResponseSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import lombok.extern.slf4j.Slf4j;

@Service("ClientServerService")
@Slf4j
public class ClientServerService extends BaseService {

	// Server fields
	private final LeshanServer server;
	private final LwM2mModel model;
	private final MagicLwM2mValueConverter converter;

	// Mapper
	private final Gson gson;

	ClientServerService(LeshanServer server) {
		this.server = server;
		this.model = new StaticModel(ObjectLoader.loadDefault());
		this.converter = new MagicLwM2mValueConverter();

		// Initialize JSON mapper
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeHierarchyAdapter(Registration.class, new RegistrationSerializer(server.getPresenceService()));
		gsonBuilder.registerTypeHierarchyAdapter(LwM2mResponse.class, new ResponseSerializer());
		gsonBuilder.registerTypeHierarchyAdapter(LwM2mNode.class, new LwM2mNodeSerializer());
		gsonBuilder.registerTypeHierarchyAdapter(LwM2mNode.class, new LwM2mNodeDeserializer());
		gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
		this.gson = gsonBuilder.create();
	}

	// Leshan Server Methods
	public void doReadRequest(HttpServletRequest req, HttpServletResponse resp, String format, Integer timeout) {

		try {
			// all registered clients
			// GET /api/clients or /api/clients/
			if (req.getRequestURI().endsWith("clients") || req.getRequestURI().endsWith("clients/")) {
				Collection<Registration> registrations = new ArrayList<>();
				for (Iterator<Registration> iterator = server.getRegistrationService().getAllRegistrations(); iterator.hasNext();) {
					registrations.add(iterator.next());
				}

				String json = this.gson.toJson(registrations.toArray(new Registration[] {}));
				resp.setContentType("application/json");
				resp.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
				resp.setStatus(HttpServletResponse.SC_OK);
				return;
			}

			String targetPath = req.getRequestURI().split("clients")[1];
			String[] path = StringUtils.split(targetPath, '/');
			if (path.length < 1) {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid path");
				return;
			}
			String clientEndpoint = path[0];

			// /endPoint : get client
			// GET /api/clients/ebraheem-fedora
			if (path.length == 1) {
				Registration registration = server.getRegistrationService().getByEndpoint(clientEndpoint);
				if (registration != null) {
					resp.setContentType("application/json");
					resp.getOutputStream().write(this.gson.toJson(registration).getBytes(StandardCharsets.UTF_8));
					resp.setStatus(HttpServletResponse.SC_OK);
				} else {
					resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					resp.getWriter().format("no registered client with id '%s'", clientEndpoint).flush();
				}
				return;
			}

			// /clients/endPoint/LWRequest/discover : do LightWeight M2M discover request on a given client.
			if (path.length >= 3 && "discover".equals(path[path.length - 1])) {
				String target = StringUtils.substringBetween(targetPath, clientEndpoint, "/discover");
				Registration registration = server.getRegistrationService().getByEndpoint(clientEndpoint);
				if (registration != null) {
					// create & process request
					DiscoverRequest request = new DiscoverRequest(target);
					DiscoverResponse cResponse = server.send(registration, request, timeout * 1000);
					processDeviceResponse(resp, cResponse);
				} else {
					resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					resp.getWriter().format("No registered client with id '%s'", clientEndpoint).flush();
				}
				return;
			}

			// /clients/endPoint/LWRequest : do LightWeight M2M read request on a given client.
			// GET /api/clients/ebraheem-fedora/1/0?format=TLV&timeout=5
			String target = StringUtils.removeStart(targetPath, "/" + clientEndpoint);
			Registration registration = server.getRegistrationService().getByEndpoint(clientEndpoint);
			if (registration != null) {
				// get content format
				ContentFormat contentFormat = format != null ? ContentFormat.fromName(format.toUpperCase()) : null;

				// create & process request
				ReadRequest request = new ReadRequest(contentFormat, target);
				ReadResponse cResponse = server.send(registration, request, timeout * 1000);
				processDeviceResponse(resp, cResponse);
			} else {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				resp.getWriter().format("No registered client with id '%s'", clientEndpoint).flush();
			}
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void doUpdateRequest(HttpServletRequest req, HttpServletResponse res, Boolean replace, String format, Integer timeout) {
		String targetPath = req.getRequestURI().split("clients")[1];
		String[] path = StringUtils.split(targetPath, '/');
		String clientEndpoint = path[0];

		try {
			// at least /endpoint/objectId/instanceId
			// PUT /api/clients/ebraheem-fedora/1/0/7?format=TLV&timeout=5
			if (path.length < 3) {
				res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid path");
				return;
			}

			// Update single object
			String target = StringUtils.removeStart(targetPath, "/" + clientEndpoint);
			Registration registration = server.getRegistrationService().getByEndpoint(clientEndpoint);
			if (registration != null) {
				if (path.length >= 3 && "attributes".equals(path[path.length - 1])) {
					// create & process request WriteAttributes request
					target = StringUtils.removeEnd(target, path[path.length - 1]);
					AttributeSet attributes = AttributeSet.parse(req.getQueryString());
					WriteAttributesRequest request = new WriteAttributesRequest(target, attributes);
					WriteAttributesResponse cResponse = server.send(registration, request, timeout * 1000);
					processDeviceResponse(res, cResponse);
				} else {

					ContentFormat contentFormat = format != null ? ContentFormat.fromName(format) : null;
					// create & process request
					LwM2mNode node = extractLwM2mNode(target, req, new LwM2mPath(target));
					WriteRequest request = new WriteRequest(replace ? Mode.REPLACE : Mode.UPDATE, contentFormat, target, node);
					WriteResponse cResponse = server.send(registration, request, timeout * 1000);
					processDeviceResponse(res, cResponse);
				}
			} else {
				res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				res.getWriter().format("No registered client with id '%s'", clientEndpoint).flush();
			}
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void doPostRequest(HttpServletRequest req, HttpServletResponse resp, String format, Integer timeout) {

		String targetPath = req.getRequestURI().split("clients")[1];
		String[] path = StringUtils.split(targetPath, '/');
		String clientEndpoint = path[0];

		ContentFormat contentFormat = format != null ? ContentFormat.fromName(format) : null;
		try {
			// /clients/endPoint/LWRequest/observe : do LightWeight M2M observe request on a given client.
			// POST /api/clients/ebraheem-fedora/1/0/0/observe?format=TLV&timeout=5
			if (path.length >= 3 && "observe".equals(path[path.length - 1])) {
				String target = StringUtils.substringBetween(targetPath, clientEndpoint, "/observe");
				Registration registration = server.getRegistrationService().getByEndpoint(clientEndpoint);
				if (registration != null) {
					// create & process request
					ObserveRequest request = new ObserveRequest(contentFormat, target);
					ObserveResponse cResponse = server.send(registration, request, timeout * 1000);
					processDeviceResponse(resp, cResponse);
				} else {
					resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					resp.getWriter().format("no registered client with id '%s'", clientEndpoint).flush();
				}
				return;
			}

			String target = StringUtils.removeStart(targetPath, "/" + clientEndpoint);

			// /clients/endPoint/LWRequest : do LightWeight M2M execute request on a given client.
			if (path.length == 4) {
				Registration registration = server.getRegistrationService().getByEndpoint(clientEndpoint);
				if (registration != null) {
					String params = null;
					if (req.getContentLength() > 0) {
						params = IOUtils.toString(req.getInputStream(), StandardCharsets.UTF_8);
					}
					ExecuteRequest request = new ExecuteRequest(target, params);
					ExecuteResponse cResponse = server.send(registration, request, timeout * 1000);
					processDeviceResponse(resp, cResponse);
				} else {
					resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					resp.getWriter().format("no registered client with id '%s'", clientEndpoint).flush();
				}
				return;
			}

			// /clients/endPoint/LWRequest : do LightWeight M2M create request on a given client.
			if (2 <= path.length && path.length <= 3) {
				Registration registration = server.getRegistrationService().getByEndpoint(clientEndpoint);
				if (registration != null) {
					// create & process request
					LwM2mNode node = extractLwM2mNode(target, req, new LwM2mPath(target));
					if (node instanceof LwM2mObjectInstance) {
						CreateRequest request;
						if (node.getId() == LwM2mObjectInstance.UNDEFINED) {
							request = new CreateRequest(contentFormat, target, ((LwM2mObjectInstance) node).getResources().values());
						} else {
							request = new CreateRequest(contentFormat, target, (LwM2mObjectInstance) node);
						}

						CreateResponse cResponse = server.send(registration, request, timeout * 1000);
						processDeviceResponse(resp, cResponse);
					} else {
						throw new IllegalArgumentException("payload must contain an object instance");
					}
				} else {
					resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					resp.getWriter().format("no registered client with id '%s'", clientEndpoint).flush();
				}

				return;
			}
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void doDeleteRequest(HttpServletRequest req, HttpServletResponse resp, Integer timeout) {
		String targetPath = req.getRequestURI().split("clients")[1];
		String[] path = StringUtils.split(targetPath, '/');
		String clientEndpoint = path[0];

		try {
			// /clients/endPoint/LWRequest/observe : cancel observation for the given resource.
			if (path.length >= 3 && "observe".equals(path[path.length - 1])) {

				String target = StringUtils.substringsBetween(targetPath, clientEndpoint, "/observe")[0];
				Registration registration = server.getRegistrationService().getByEndpoint(clientEndpoint);
				if (registration != null) {
					server.getObservationService().cancelObservations(registration, target);
					resp.setStatus(HttpServletResponse.SC_OK);
				} else {
					resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					resp.getWriter().format("no registered client with id '%s'", clientEndpoint).flush();
				}
				return;
			}

			// /clients/endPoint/LWRequest/ : delete instance
			String target = StringUtils.removeStart(targetPath, "/" + clientEndpoint);
			Registration registration = server.getRegistrationService().getByEndpoint(clientEndpoint);
			if (registration != null) {
				DeleteRequest request = new DeleteRequest(target);
				DeleteResponse cResponse = server.send(registration, request, timeout * 1000);
				processDeviceResponse(resp, cResponse);
			} else {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				resp.getWriter().format("no registered client with id '%s'", clientEndpoint).flush();
			}
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private void processDeviceResponse(HttpServletResponse resp, LwM2mResponse cResponse) {
		try {
			if (cResponse == null) {
				resp.setStatus(HttpServletResponse.SC_GATEWAY_TIMEOUT);
				resp.getWriter().append("Request timeout").flush();
			} else {
				String response = this.gson.toJson(cResponse);
				resp.setContentType("application/json");
				resp.getOutputStream().write(response.getBytes());
				resp.setStatus(HttpServletResponse.SC_OK);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private LwM2mNode extractLwM2mNode(String target, HttpServletRequest req, LwM2mPath path) {
		try {
			String contentType = StringUtils.substringBefore(req.getContentType(), ";");
			if ("application/json".equals(contentType)) {
				String content = IOUtils.toString(req.getInputStream(), req.getCharacterEncoding());
				LwM2mNode node;
				try {
					node = gson.fromJson(content, LwM2mNode.class);
					if (node instanceof LwM2mSingleResource) {
						// TODO HACK resource type should be extracted from json value but this is not yet available.
						LwM2mSingleResource singleResource = (LwM2mSingleResource) node;
						ResourceModel resourceModel = model.getResourceModel(path.getObjectId(), singleResource.getId());
						if (resourceModel != null) {
							Type expectedType = resourceModel.type;
							Object expectedValue = converter.convertValue(singleResource.getValue(), singleResource.getType(), expectedType,
									path);
							node = LwM2mSingleResource.newResource(node.getId(), expectedValue, expectedType);
						}
					}
				} catch (JsonSyntaxException e) {
					throw new InvalidRequestException(e, "unable to parse json to tlv:%s", e.getMessage());
				}
				return node;

			} else if ("text/plain".equals(contentType)) {
				String content = IOUtils.toString(req.getInputStream(), req.getCharacterEncoding());
				int rscId = Integer.valueOf(target.substring(target.lastIndexOf("/") + 1));
				return LwM2mSingleResource.newStringResource(rscId, content);

			} else {
				throw new InvalidRequestException("content type %s not supported", req.getContentType());
			}
		} catch (InvalidRequestException ie) {
			throw ie;

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
