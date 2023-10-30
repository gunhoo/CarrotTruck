package com.boyworld.carrot.api.service.survey;

import com.boyworld.carrot.api.controller.survey.request.CreateSurveyRequest;
import com.boyworld.carrot.api.controller.survey.response.CreateSurveyResponse;
import com.boyworld.carrot.api.controller.survey.response.SurveyDetailsResponse;
import com.boyworld.carrot.api.controller.survey.response.SurveyCountResponse;
import com.boyworld.carrot.domain.survey.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 수요조사 서비스
 *
 * @author 양진형
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class SurveyService {

    private final SurveyRepository surveyRepository;

    /**
     * 설문 제출
     *
     * @param request 제출 수요조사 정보
     * @return 제출 수요조사 정보
     */
    public CreateSurveyResponse createSurvey(CreateSurveyRequest request) {

        // GeocodingUtil 사용
        return null;
    }

    /**
     * 수요조사 삭제
     *
     * @param surveyId 삭제할 수요조사 ID
     * @return 삭제한 수요조사 ID
     */
    public Long deleteSurvey(Long surveyId) {
        return null;
    }

}
