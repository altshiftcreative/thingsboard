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
}
