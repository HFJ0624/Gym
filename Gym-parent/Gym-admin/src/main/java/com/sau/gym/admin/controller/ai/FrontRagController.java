package com.sau.gym.admin.controller.ai;


import com.sau.gym.admin.rag.service.RagQaService;
import com.sau.gym.model.dto.rag.RagAskDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.vo.rag.RagAnswerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/30 17:02
 */
@RestController
@RequestMapping("/front/rag")
public class FrontRagController {

    @Autowired
    private RagQaService ragQaService;

    /***
     *
     * @param dto 用户RAG问答请求
     * @return RAG问答接口
     */
    @PostMapping("/ask")
    public Result ask(@RequestBody RagAskDto dto) {
        RagAnswerVO answer = ragQaService.ask(dto);
        return Result.build(answer, ResultCodeEnum.SUCCESS);
    }
}
