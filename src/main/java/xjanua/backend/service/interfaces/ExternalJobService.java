package xjanua.backend.service.interfaces;

import java.time.Instant;

public interface ExternalJobService {
    boolean checkHandleProduct();

    void callSetJob(String id, String apiName, String jsonData, Instant runTime);

    void callDeleteJob(String id);

    String callToDotnet(String url, String jsonData);
}
