package org.thingsboard.server.transport.http;
import java.util.concurrent.ForkJoinPool;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
@RestController
@ConditionalOnExpression("'${service.type:null}'=='tb-transport' || ('${service.type:null}'=='monolith' && '${transport.api_enabled:true}'=='true' && '${transport.http.enabled}'=='true')")
@RequestMapping("/api/v1")
@Slf4j
public class Tr069ApiController {
	
	@Value("${genie.url}")
	private String genieBaseUrl;
	
	@Value("${genie.username}")
	private String genieUsername;
	
	@Value("${genie.password}")
	private String geniePassword;
	
    public String getToken(){
        String tokenResponse = "";
        try {
            OkHttpClient client = new OkHttpClient();
            FormBody formBody = new FormBody.Builder()
                    .add("username", genieUsername)
                    .add("password", geniePassword)
                    .build();
            Request request = new Request.Builder()
                    .url(genieBaseUrl + "/login")
                    .post(formBody)
                    .build();
            Response response = client.newCall(request).execute();
            tokenResponse = response.body().string();
            System.out.println(tokenResponse);
        }catch (Exception e){
            e.printStackTrace();
        }
        return tokenResponse;
    }
    private String getTR69DeviceList(){
        String acsResponse = null;
        try{
            String token = getToken();
            token = token.replaceAll("^\"|\"$", "");
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(genieBaseUrl + "/tr69/api/devices")
                    .addHeader("Cookie","genieacs-ui-jwt="+token)
                    .build();
            Response  response = client.newCall(request).execute();
            acsResponse = response.body().string();
        }catch (Exception e){
        }
        return acsResponse;
    }

    private String getTR69Faults(){
        String acsResponse = null;
        try{
            String token = getToken();
            token = token.replaceAll("^\"|\"$", "");
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(genieBaseUrl + "/tr69/api/faults")
                    .addHeader("Cookie","genieacs-ui-jwt="+token)
                    .build();
            Response  response = client.newCall(request).execute();
            acsResponse = response.body().string();
        }catch (Exception e){
        }
        return acsResponse;
    }

    private String getTR69Presets(){
        String acsResponse = null;
        try{
            String token = getToken();
            token = token.replaceAll("^\"|\"$", "");
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(genieBaseUrl + "/tr69/api/presets")
                    .addHeader("Cookie","genieacs-ui-jwt="+token)
                    .build();
            Response  response = client.newCall(request).execute();
            acsResponse = response.body().string();
        }catch (Exception e){
        }
        return acsResponse;
    }

    private String getTR69Provisions(){
        String acsResponse = null;
        try{
            String token = getToken();
            token = token.replaceAll("^\"|\"$", "");
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(genieBaseUrl + "/tr69/api/provisions")
                    .addHeader("Cookie","genieacs-ui-jwt="+token)
                    .build();
            Response  response = client.newCall(request).execute();
            acsResponse = response.body().string();
        }catch (Exception e){
        }
        return acsResponse;
    }

    private String getTR69Config(){
        String acsResponse = null;
        try{
            String token = getToken();
            token = token.replaceAll("^\"|\"$", "");
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(genieBaseUrl + "/tr69/api/config")
                    .addHeader("Cookie","genieacs-ui-jwt="+token)
                    .build();
            Response  response = client.newCall(request).execute();
            acsResponse = response.body().string();
        }catch (Exception e){
        }
        return acsResponse;
    }
    private void deleteTR69DeviceById(String deviceId){
        try{
            String token = getToken();
            token = token.replaceAll("^\"|\"$", "");
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(genieBaseUrl + "/tr69/api/devices/"+deviceId)
                    .addHeader("Cookie","genieacs-ui-jwt="+token)
                    .delete()
                    .build();
            client.newCall(request).execute();
        }catch (Exception e){
        }
    }

    private void deleteTR69FaultsById(String faultsId){
        try{
            String token = getToken();
            token = token.replaceAll("^\"|\"$", "");
            OkHttpClient client = new OkHttpClient();

            URIBuilder ub = new URIBuilder(genieBaseUrl + "/tr69/api/faults/" + faultsId);
//            ub.addParameter("filter", "LOWER(DeviceID.SerialNumber) LIKE \"%" + faultsId + "%\"");
            String url = ub.toString();

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Cookie","genieacs-ui-jwt="+token)
                    .delete()
                    .build();
            client.newCall(request).execute();
        }catch (Exception e){
        }
    }
    private void deleteTR69PresetsById(String presetsId){
        try{
            String token = getToken();
            token = token.replaceAll("^\"|\"$", "");
            OkHttpClient client = new OkHttpClient();
            URIBuilder ub = new URIBuilder(genieBaseUrl + "/tr69/api/presets/" + presetsId);
            String url = ub.toString();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Cookie","genieacs-ui-jwt="+token)
                    .delete()
                    .build();
            client.newCall(request).execute();
        }catch (Exception e){
        }
    }
    private void deleteTR69ProvisionsById(String provisionsId){
        try{
            String token = getToken();
            token = token.replaceAll("^\"|\"$", "");
            OkHttpClient client = new OkHttpClient();
            URIBuilder ub = new URIBuilder(genieBaseUrl + "/tr69/api/provisions/" + provisionsId);
            String url = ub.toString();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Cookie","genieacs-ui-jwt="+token)
                    .delete()
                    .build();
            client.newCall(request).execute();
        }catch (Exception e){
        }
    }
    private void deleteTR69ConfigById(String configId){
        try{
            String token = getToken();
            token = token.replaceAll("^\"|\"$", "");
            OkHttpClient client = new OkHttpClient();
            URIBuilder ub = new URIBuilder(genieBaseUrl + "/tr69/api/config/" + configId);
            String url = ub.toString();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Cookie","genieacs-ui-jwt="+token)
                    .delete()
                    .build();
            client.newCall(request).execute();
        }catch (Exception e){
        }
    }

    private String editTasks(String taskRequest, String deviceID){
        String taskResponse = "";
        try{
            String token = getToken();
            token = token.replaceAll("^\"|\"$", "");
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            okhttp3.RequestBody formBody =  okhttp3.RequestBody.create(JSON,taskRequest);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(genieBaseUrl + "/tr69/api/devices/"+deviceID+"/tasks")
                    .addHeader("Cookie","genieacs-ui-jwt="+token)
                    .post(formBody)
                    .build();
            Response  response = client.newCall(request).execute();
            taskResponse = response.body().string();
        }catch (Exception e){
        }
        return taskResponse;
    }
    

    private String addTag(String tagRequest, String deviceID){
        String tagResponse = "";
        try{
            String token = getToken();
            token = token.replaceAll("^\"|\"$", "");
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            okhttp3.RequestBody formBody =  okhttp3.RequestBody.create(JSON,tagRequest);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(genieBaseUrl + "/tr69/api/devices/" + deviceID + "/tags")
                    .addHeader("Cookie","genieacs-ui-jwt="+token)
                    .post(formBody)
                    .build();
            Response  response = client.newCall(request).execute();
            tagResponse = response.body().string();
        }catch (Exception e){
        }
        return tagResponse;
    }
    private String newPresets(String presetsRequest, String presetsId){
        String tagResponse = "";
        try{
            String token = getToken();
            token = token.replaceAll("^\"|\"$", "");
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            okhttp3.RequestBody formBody =  okhttp3.RequestBody.create(JSON,presetsRequest);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(genieBaseUrl + "/tr69/api/presets/" + presetsId)
                    .addHeader("Cookie","genieacs-ui-jwt="+token)
                    .put(formBody)
                    .build();
            Response  response = client.newCall(request).execute();
            tagResponse = response.body().string();
        }catch (Exception e){
        }
        return tagResponse;
    }
    private String newProvisions(String presetsRequest, String presetsId){
        String tagResponse = "";
        try{
            String token = getToken();
            token = token.replaceAll("^\"|\"$", "");
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            okhttp3.RequestBody formBody =  okhttp3.RequestBody.create(JSON,presetsRequest);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(genieBaseUrl + "/tr69/api/provisions/" + presetsId)
                    .addHeader("Cookie","genieacs-ui-jwt="+token)
                    .put(formBody)
                    .build();
            Response  response = client.newCall(request).execute();
            tagResponse = response.body().string();
        }catch (Exception e){
        }
        return tagResponse;
    }

    private String newConfig(String configRequest, String configId){
        String tagResponse = "";
        try{
            String token = getToken();
            token = token.replaceAll("^\"|\"$", "");
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            okhttp3.RequestBody formBody =  okhttp3.RequestBody.create(JSON,configRequest);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(genieBaseUrl + "/tr69/api/config/" + configId)
                    .addHeader("Cookie","genieacs-ui-jwt="+token)
                    .put(formBody)
                    .build();
            Response  response = client.newCall(request).execute();
            tagResponse = response.body().string();
        }catch (Exception e){
        }
        return tagResponse;
    }
//    http://localhost:3000/api/devices/?filter=LOWER(DeviceID.SerialNumber)%20LIKE%20%22000001%22
    private String search(String serialNumber){
        String acsResponse = null;
        try{
            String token = getToken();
            token = token.replaceAll("^\"|\"$", "");
            OkHttpClient client = new OkHttpClient();
            URIBuilder ub = new URIBuilder(genieBaseUrl + "/tr69/api/devices/");
            ub.addParameter("filter", "LOWER(DeviceID.SerialNumber) LIKE \"%" + serialNumber + "%\"");
            String url = ub.toString();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Cookie","genieacs-ui-jwt="+token)
                    .build();
            Response  response = client.newCall(request).execute();
            acsResponse = response.body().string();
        }catch (Exception e){
        }
        return acsResponse;
    }
    private String searchFaults(String device){
        String acsResponse = null;
        try{
            String token = getToken();
            token = token.replaceAll("^\"|\"$", "");
            OkHttpClient client = new OkHttpClient();
            URIBuilder ub = new URIBuilder(genieBaseUrl + "/tr69/api/faults/");
            ub.addParameter("filter", "LOWER(device) LIKE \"%" + device + "%\"");
            String url = ub.toString();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Cookie","genieacs-ui-jwt="+token)
                    .build();
            Response  response = client.newCall(request).execute();
            acsResponse = response.body().string();
        }catch (Exception e){
        }
        return acsResponse;
    }
    private String searchPresets(String _id){
        String acsResponse = null;
        try{
            String token = getToken();
            token = token.replaceAll("^\"|\"$", "");
            OkHttpClient client = new OkHttpClient();
            URIBuilder ub = new URIBuilder(genieBaseUrl + "/tr69/api/presets/");
            ub.addParameter("filter", "LOWER(_id) LIKE \"%" + _id + "%\"");
            String url = ub.toString();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Cookie","genieacs-ui-jwt="+token)
                    .build();
            Response  response = client.newCall(request).execute();
            acsResponse = response.body().string();
        }catch (Exception e){
        }
        return acsResponse;
    }
    private String searchProvisions(String _id){
        String acsResponse = null;
        try{
            String token = getToken();
            token = token.replaceAll("^\"|\"$", "");
            OkHttpClient client = new OkHttpClient();
            URIBuilder ub = new URIBuilder(genieBaseUrl + "/tr69/api/provisions/");
            ub.addParameter("filter", "LOWER(_id) LIKE \"%" + _id + "%\"");
            String url = ub.toString();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Cookie","genieacs-ui-jwt="+token)
                    .build();
            Response  response = client.newCall(request).execute();
            acsResponse = response.body().string();
        }catch (Exception e){
        }
        return acsResponse;
    }
    ////////////////////////////////////////////////////////////////////
    //getTR69Faults
    @RequestMapping(value = "/tr69/devices", method = RequestMethod.GET,produces = "application/json")
    public DeferredResult<ResponseEntity<?>> getTr069Devices() {
        System.out.println("Received async-deferredresult request");
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            System.out.println("Processing in separate thread");
            String res =  getTR69DeviceList();
            output.setResult(new ResponseEntity<String>(
                    res,
                    HttpStatus.OK));
        });
        return output;
    }


    @RequestMapping(value = "/tr69/presets", method = RequestMethod.GET,produces = "application/json")
    public DeferredResult<ResponseEntity<?>> getTr069Presets() {
        System.out.println("Received async-deferredresult request");
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            System.out.println("Processing in separate thread");
            String res =  getTR69Presets();
            output.setResult(new ResponseEntity<String>(
                    res,
                    HttpStatus.OK));
        });
        return output;
    }

    @RequestMapping(value = "/tr69/faults", method = RequestMethod.GET,produces = "application/json")
    public DeferredResult<ResponseEntity<?>> getTr069Faults() {
        System.out.println("Received async-deferredresult request");
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            System.out.println("Processing in separate thread");
            String res =  getTR69Faults();
            output.setResult(new ResponseEntity<String>(
                    res,
                    HttpStatus.OK));
        });
        return output;
    }
    //getTR69Config
    @RequestMapping(value = "/tr69/provisions", method = RequestMethod.GET,produces = "application/json")
    public DeferredResult<ResponseEntity<?>> getTr069Provisions() {
        System.out.println("Received async-deferredresult request");
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            System.out.println("Processing in separate thread");
            String res =  getTR69Provisions();
            output.setResult(new ResponseEntity<String>(
                    res,
                    HttpStatus.OK));
        });
        return output;
    }

    @RequestMapping(value = "/tr69/config", method = RequestMethod.GET,produces = "application/json")
    public DeferredResult<ResponseEntity<?>> getTr069Config() {
        System.out.println("Received async-deferredresult request");
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            System.out.println("Processing in separate thread");
            String res =  getTR69Config();
            output.setResult(new ResponseEntity<String>(
                    res,
                    HttpStatus.OK));
        });
        return output;
    }

    @RequestMapping(value = "/tr69/faults", method = RequestMethod.DELETE,produces = "application/json")
    public DeferredResult<ResponseEntity<?>> deleteTr069Faults(@RequestParam(value = "faultsId", required = true, defaultValue = "") String faultsId) {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            deleteTR69FaultsById(faultsId);
            output.setResult(new ResponseEntity<String>(

                    HttpStatus.OK));
        });
        return output;
    }
    @RequestMapping(value = "/tr69/presets", method = RequestMethod.DELETE,produces = "application/json")
    public DeferredResult<ResponseEntity<?>> deleteTr069Presets(@RequestParam(value = "presetsId", required = true, defaultValue = "") String presetsId) {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            deleteTR69PresetsById(presetsId);
            output.setResult(new ResponseEntity<String>(

                    HttpStatus.OK));
        });
        return output;
    }
    @RequestMapping(value = "/tr69/provisions", method = RequestMethod.DELETE,produces = "application/json")
    public DeferredResult<ResponseEntity<?>> deleteTr069Provisions(@RequestParam(value = "provisionsId", required = true, defaultValue = "") String provisionsId) {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            deleteTR69ProvisionsById(provisionsId);
            output.setResult(new ResponseEntity<String>(

                    HttpStatus.OK));
        });
        return output;
    }
    @RequestMapping(value = "/tr69/config", method = RequestMethod.DELETE,produces = "application/json")
    public DeferredResult<ResponseEntity<?>> deleteTr069Config(@RequestParam(value = "configId", required = true, defaultValue = "") String configId) {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            deleteTR69ConfigById(configId);
            output.setResult(new ResponseEntity<String>(

                    HttpStatus.OK));
        });
        return output;
    }

    @RequestMapping(value = "/tr69/devices", method = RequestMethod.DELETE,produces = "application/json")
    public DeferredResult<ResponseEntity<?>> deleteTr069Devices(@RequestParam(value = "deviceID", required = true, defaultValue = "") String deviceID) {
        System.out.println("Received async-deferredresult request");
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            System.out.println("Processing in separate thread");
            deleteTR69DeviceById(deviceID);
            output.setResult(new ResponseEntity<String>(

                    HttpStatus.OK));
        });
        return output;
    }

    @RequestMapping(value = "/tr69/tasks", method = RequestMethod.POST,produces = "application/json")
    public DeferredResult<ResponseEntity<?>> editDeviceValues(@RequestBody String deviceTask,@RequestParam(value = "deviceID", required = true, defaultValue = "") String deviceID) {
        System.out.println("Received async-deferredresult request");
        System.out.println("task: "+deviceTask);
        System.out.println("device ID: "+deviceID);
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            System.out.println("Processing in separate thread");
            String ResString = editTasks(deviceTask,deviceID);
            output.setResult(new ResponseEntity<>(
                    ResString,
                    HttpStatus.OK));
        });
        return output;
    }


    @RequestMapping(value = "/tr69/tag", method = RequestMethod.POST,produces = "application/json")
    public DeferredResult<ResponseEntity<?>> tagDevice(@RequestBody String deviceTag,@RequestParam(value = "deviceID", required = true, defaultValue = "") String deviceID) {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            String ResString = addTag(deviceTag,deviceID);
            output.setResult(new ResponseEntity<>(
                    ResString,
                    HttpStatus.OK));
        });
        return output;
    }
    @RequestMapping(value = "/tr69/presets", method = RequestMethod.PUT,produces = "application/json")
    public DeferredResult<ResponseEntity<?>> newAdminPresets(@RequestBody String presetsRequest,@RequestParam(value = "presetsId", required = true, defaultValue = "") String presetsId) {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            String ResString = newPresets(presetsRequest,presetsId);
            output.setResult(new ResponseEntity<>(
                    ResString,
                    HttpStatus.OK));
        });
        return output;
    }
    //newConfig
    @RequestMapping(value = "/tr69/provisions", method = RequestMethod.PUT,produces = "application/json")
    public DeferredResult<ResponseEntity<?>> newAdminProvisions(@RequestBody String provisionsRequest,@RequestParam(value = "provisionsId", required = true, defaultValue = "") String provisionsId) {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            String ResString = newProvisions(provisionsRequest,provisionsId);
            output.setResult(new ResponseEntity<>(
                    ResString,
                    HttpStatus.OK));
        });
        return output;
    }
    @RequestMapping(value = "/tr69/config", method = RequestMethod.PUT,produces = "application/json")
    public DeferredResult<ResponseEntity<?>> newAdminConfig(@RequestBody String configRequest,@RequestParam(value = "configId", required = true, defaultValue = "") String configId) {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            String ResString = newConfig(configRequest,configId);
            output.setResult(new ResponseEntity<>(
                    ResString,
                    HttpStatus.OK));
        });
        return output;
    }

    @RequestMapping(value = "/tr69/search", method = RequestMethod.GET,produces = "application/json")
    public DeferredResult<ResponseEntity<?>> searchDevice(@RequestParam(value = "serialNumber", required = true, defaultValue = "") String serialNumber) {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            String ResString = search(serialNumber);
            output.setResult(new ResponseEntity<>(
                    ResString,
                    HttpStatus.OK));
        });
        return output;
    }
    @RequestMapping(value = "/tr69/searchfaults", method = RequestMethod.GET,produces = "application/json")
    public DeferredResult<ResponseEntity<?>> searchDeviceFaults(@RequestParam(value = "device", required = true, defaultValue = "") String device) {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            String ResString = searchFaults(device);
            output.setResult(new ResponseEntity<>(
                    ResString,
                    HttpStatus.OK));
        });
        return output;
    }

    @RequestMapping(value = "/tr69/searchpresets", method = RequestMethod.GET,produces = "application/json")
    public DeferredResult<ResponseEntity<?>> searchAdminPresets(@RequestParam(value = "_id", required = true, defaultValue = "") String _id) {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            String ResString = searchPresets(_id);
            output.setResult(new ResponseEntity<>(
                    ResString,
                    HttpStatus.OK));
        });
        return output;
    }
    @RequestMapping(value = "/tr69/searchprovisions", method = RequestMethod.GET,produces = "application/json")
    public DeferredResult<ResponseEntity<?>> searchAdminProvisions(@RequestParam(value = "_id", required = true, defaultValue = "") String _id) {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            String ResString = searchProvisions(_id);
            output.setResult(new ResponseEntity<>(
                    ResString,
                    HttpStatus.OK));
        });
        return output;
    }

//searchProvisions

}