package services.rest;

import android.content.res.Resources;

import com.moje.jobclient.app.R;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import rest.JobSearchResult;

/**
 * Created by ivan on 28.12.14..
 */
public class RestService {

    private RestTemplate restTemplate;

    public RestService() {
        this.restTemplate = new RestTemplate();
    }

    public <T> List getList (String url, Class<T[]> responseType) {
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        ResponseEntity<T[]> jobList = restTemplate.getForEntity(url, responseType);
        return Arrays.asList(jobList.getBody());
    }

    public <T> T getItem (String url, Class<T> responseType) {
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return restTemplate.getForObject(url, responseType);
    }

    public String postData (String url, MultiValueMap<String, String> paramsMap) {

        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        return restTemplate.postForObject(url, paramsMap, String.class);
    }
}
