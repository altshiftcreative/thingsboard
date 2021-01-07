package org.thingsboard.server.transport.http;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import java.util.concurrent.ForkJoinPool;

@RestController
@ConditionalOnExpression("'${service.type:null}'=='tb-transport' || ('${service.type:null}'=='monolith' && '${transport.api_enabled:true}'=='true' && '${transport.http.enabled}'=='true')")
@RequestMapping("/api/v1")
@Slf4j
public class LwM2MApiController {

    private String getClientsLw(){
        String acsResponse = null;
        try{
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://localhost:9090/api/clients")
                    .build();
            Response  response = client.newCall(request).execute();
            acsResponse = response.body().string();
        }catch (Exception e){
        }
        return acsResponse;
    }

    private String getClientsDataLw(String endpoint){
        String acsResponse = null;
        try{
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://localhost:9090/api/objectspecs/"+endpoint)
                    .build();
            Response  response = client.newCall(request).execute();
            acsResponse = response.body().string();
        }catch (Exception e){
        }
        return acsResponse;
    }

    private String getCertificateLw(){
        String acsResponse = null;
        try{
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://localhost:9090/api/security/server")
                    .build();
            Response  response = client.newCall(request).execute();
            acsResponse = response.body().string();
        }catch (Exception e){
        }
        return acsResponse;
    }

//    getClientsDataLw
//////////////////////////////////////////////////////////////////////////////////////////////////////
    @RequestMapping(value = "/Lw/clients", method = RequestMethod.GET,produces = "application/json")
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

    @RequestMapping(value = "/Lw/clientsData", method = RequestMethod.GET,produces = "application/json")
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
    @RequestMapping(value = "/Lw/certificate", method = RequestMethod.GET,produces = "application/json")
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
}
