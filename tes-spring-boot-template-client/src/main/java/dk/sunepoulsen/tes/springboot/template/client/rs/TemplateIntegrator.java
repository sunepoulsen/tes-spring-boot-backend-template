package dk.sunepoulsen.tes.springboot.template.client.rs;

import dk.sunepoulsen.tes.springboot.client.core.rs.integrations.TechEasySolutionsBackendIntegrator;
import dk.sunepoulsen.tes.springboot.client.core.rs.integrations.TechEasySolutionsClient;
import dk.sunepoulsen.tes.springboot.template.client.rs.model.TemplateModel;
import io.reactivex.Single;

public class TemplateIntegrator extends TechEasySolutionsBackendIntegrator {
    public TemplateIntegrator(TechEasySolutionsClient httpClient) {
        super(httpClient);
    }

    public Single<TemplateModel> create(TemplateModel model) {
        return Single.fromFuture(httpClient.post("/templates", model, TemplateModel.class))
                .onErrorResumeNext(this::mapClientExceptions);
    }

}
