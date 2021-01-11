package org.thingsboard.server.transport.http;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.ForkJoinPool;

@RestController
@ConditionalOnExpression("'${service.type:null}'=='tb-transport' || ('${service.type:null}'=='monolith' && '${transport.api_enabled:true}'=='true' && '${transport.http.enabled}'=='true')")
@RequestMapping("/api/v1")
@Slf4j
public class LwM2MApiController {

    private String getClientsLw() {
        String acsResponse = null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://localhost:9090/api/clients")
                    .build();
            Response response = client.newCall(request).execute();
            acsResponse = response.body().string();
        } catch (Exception e) {
        }
        return acsResponse;
    }

    private String getClientsDataLw(String endpoint) {
        String acsResponse = null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://localhost:9090/api/objectspecs/" + endpoint)
                    .build();
            Response response = client.newCall(request).execute();
            acsResponse = response.body().string();
        } catch (Exception e) {
        }
        return acsResponse;
    }

    private String getCertificateLw() {
        String acsResponse = null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://localhost:9090/api/security/server")
                    .build();
            Response response = client.newCall(request).execute();
            acsResponse = response.body().string();
        } catch (Exception e) {
        }
        return acsResponse;
    }


    private String readDataLw(String endpoint, String[] value, String format, String timeOut) {
        String acsResponse = null;
        String urlLink="http://localhost:9090/api/clients/" + endpoint;
        for(int i=0;i< value.length;i++){
            urlLink += "/"+value[i];
        }
        urlLink+= "?format=" + format + "&timeout=" + timeOut;
        System.out.println("the Linkkk:     "+urlLink);
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://localhost:9090/api/clients/" + endpoint + "/" + value[0] + "/" + value[1] + "/" + value[2] + "?format=" + format + "&timeout=" + timeOut)
                    .build();
            Response response = client.newCall(request).execute();
            acsResponse = response.body().string();
        } catch (Exception e) {
        }
        return acsResponse;
    }


    private String writeDataLw(String writeChanged, String endpoint, String[] value, String format, String timeOut) {
        String acsResponse = "";
        try {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            okhttp3.RequestBody formBody = okhttp3.RequestBody.create(JSON, writeChanged);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://localhost:9090/api/clients/" + endpoint + "/" + value[0] + "/" + value[1] + "/" + value[2] + "?format=" + format + "&timeout=" + timeOut)
                    .put(formBody)
                    .build();
            Response response = client.newCall(request).execute();
            acsResponse = response.body().string();
        } catch (Exception e) {
        }
        return acsResponse;
    }


    private String observeDataLw(String observe, String endpoint, String[] value, String format, String timeOut) {
        String acsResponse = "";
        try {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            okhttp3.RequestBody formBody = okhttp3.RequestBody.create(JSON, observe);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://localhost:9090/api/clients/" + endpoint + "/" + value[0] + "/" + value[1] + "/" + value[2] + "/observe?format=" + format + "&timeout=" + timeOut)
                    .post(formBody)
                    .build();
            Response response = client.newCall(request).execute();
            acsResponse = response.body().string();
        } catch (Exception e) {
        }
        return acsResponse;
    }


    private String stopObserveDataLw(String endpoint, String[] value) {
        String acsResponse = "";
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://localhost:9090/api/clients/" + endpoint + "/" + value[0] + "/" + value[1] + "/" + value[2] + "/observe")
                    .delete()
                    .build();
            Response response = client.newCall(request).execute();
            acsResponse = response.body().string();
        } catch (Exception e) {
        }
        return acsResponse;
    }

//    http://localhost:9090/api/clients/BW-Client-5/1/0/8?timeout=1800
//    executeDataLw

    private String executeDataLw(String execute, String endpoint, String[] value, String timeOut) {
        String acsResponse = "";
        try {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            okhttp3.RequestBody formBody = okhttp3.RequestBody.create(JSON, execute);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://localhost:9090/api/clients/" + endpoint + "/" + value[0] + "/" + value[1] + "/" + value[2] + "?timeout=" + timeOut)
                    .post(formBody)
                    .build();
            Response response = client.newCall(request).execute();
            acsResponse = response.body().string();
        } catch (Exception e) {
        }
        return acsResponse;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @RequestMapping(value = "/Lw/clients", method = RequestMethod.GET, produces = "application/json")
    public DeferredResult<ResponseEntity<?>> getClients() {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            String ResString = getClientsLw();
            output.setResult(new ResponseEntity<>(
                    ResString,
                    HttpStatus.OK));
        });
        return output;
    }

    @RequestMapping(value = "/Lw/clientsData", method = RequestMethod.GET, produces = "application/json")
    public DeferredResult<ResponseEntity<?>> getClientsData(@RequestParam(value = "endpoint", required = true, defaultValue = "") String endpoint) {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            String ResString = getClientsDataLw(endpoint);
            output.setResult(new ResponseEntity<>(
                    ResString,
                    HttpStatus.OK));
        });
        return output;
    }

    @RequestMapping(value = "/Lw/certificate", method = RequestMethod.GET, produces = "application/json")
    public DeferredResult<ResponseEntity<?>> getCertificate() {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            String ResString = getCertificateLw();
            output.setResult(new ResponseEntity<>(
                    ResString,
                    HttpStatus.OK));
        });
        return output;
    }

    @RequestMapping(value = "/Lw/read", method = RequestMethod.GET, produces = "application/json")
    public DeferredResult<ResponseEntity<?>> readData(
            @RequestParam(value = "endpoint", required = true, defaultValue = "") String endpoint,
            @RequestParam(value = "value", required = true, defaultValue = "") String[] value,
            @RequestParam(value = "format", required = true, defaultValue = "") String format,
            @RequestParam(value = "timeOut", required = true, defaultValue = "") String timeOut) {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            String ResString = readDataLw(endpoint, value, format, timeOut);
            output.setResult(new ResponseEntity<>(
                    ResString,
                    HttpStatus.OK));
        });
        return output;
    }

    @RequestMapping(value = "/Lw/write", method = RequestMethod.PUT, produces = "application/json")
    public DeferredResult<ResponseEntity<?>> writeData(@RequestBody String writeChanged,
                                                       @RequestParam(value = "endpoint", required = true, defaultValue = "") String endpoint,
                                                       @RequestParam(value = "value", required = true, defaultValue = "") String[] value,
                                                       @RequestParam(value = "format", required = true, defaultValue = "") String format,
                                                       @RequestParam(value = "timeOut", required = true, defaultValue = "") String timeOut) {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            String ResString = writeDataLw(writeChanged, endpoint, value, format, timeOut);
            output.setResult(new ResponseEntity<>(
                    ResString,
                    HttpStatus.OK));
        });
        return output;
    }

    @RequestMapping(value = "/Lw/observe", method = RequestMethod.POST, produces = "application/json")
    public DeferredResult<ResponseEntity<?>> observeData(@RequestBody String observe,
                                                         @RequestParam(value = "endpoint", required = true, defaultValue = "") String endpoint,
                                                         @RequestParam(value = "value", required = true, defaultValue = "") String[] value,
                                                         @RequestParam(value = "timeOut", required = true, defaultValue = "") String timeOut) {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            String ResString = executeDataLw(observe, endpoint, value, timeOut);
            output.setResult(new ResponseEntity<>(
                    ResString,
                    HttpStatus.OK));
        });
        return output;
    }

    @RequestMapping(value = "/Lw/observe", method = RequestMethod.DELETE, produces = "application/json")
    public DeferredResult<ResponseEntity<?>> stopObserveData(
            @RequestParam(value = "endpoint", required = true, defaultValue = "") String endpoint,
            @RequestParam(value = "value", required = true, defaultValue = "") String[] value
    ) {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            String ResString = stopObserveDataLw(endpoint, value);
            output.setResult(new ResponseEntity<>(
                    ResString,
                    HttpStatus.OK));
        });
        return output;
    }

    @RequestMapping(value = "/Lw/execute", method = RequestMethod.POST, produces = "application/json")
    public DeferredResult<ResponseEntity<?>> executeData(@RequestBody String execute,
                                                         @RequestParam(value = "endpoint", required = true, defaultValue = "") String endpoint,
                                                         @RequestParam(value = "value", required = true, defaultValue = "") String[] value,
                                                         @RequestParam(value = "format", required = true, defaultValue = "") String format,
                                                         @RequestParam(value = "timeOut", required = true, defaultValue = "") String timeOut) {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            String ResString = observeDataLw(execute, endpoint, value, format, timeOut);
            output.setResult(new ResponseEntity<>(
                    ResString,
                    HttpStatus.OK));
        });
        return output;
    }
}
