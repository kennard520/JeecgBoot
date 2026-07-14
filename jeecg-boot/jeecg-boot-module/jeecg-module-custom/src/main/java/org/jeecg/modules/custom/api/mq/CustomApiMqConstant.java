package org.jeecg.modules.custom.api.mq;

public interface CustomApiMqConstant {
    String PARSE_REQUEST_EXCHANGE = "customs.parse.request.exchange";
    String PARSE_REQUEST_QUEUE_PREFIX = "customs.parse.request.";

    String PARSE_RESULT_EXCHANGE = "customs.parse.result.exchange";
    String PARSE_RESULT_QUEUE = "customs.parse.result.java";
    String PARSE_RESULT_ROUTING_KEY = "parse.result";
    String PARSE_RESULT_DLX = "customs.parse.result.dlx";
    String PARSE_RESULT_DLQ = "customs.parse.result.dlq";
    String PARSE_RESULT_DLQ_ROUTING_KEY = "parse.result.dlq";
}
