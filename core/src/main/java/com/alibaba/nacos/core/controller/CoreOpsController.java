
package com.alibaba.nacos.core.controller;

import com.alibaba.nacos.common.model.RestResult;
import com.alibaba.nacos.common.model.RestResultUtils;
import com.alibaba.nacos.consistency.IdGenerator;
import com.alibaba.nacos.core.distributed.ProtocolManager;
import com.alibaba.nacos.core.distributed.id.IdGeneratorManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/core/ops")
public class CoreOpsController {

    private final ProtocolManager protocolManager;

    private final IdGeneratorManager idGeneratorManager;

    public CoreOpsController(ProtocolManager protocolManager, IdGeneratorManager idGeneratorManager) {
        this.protocolManager = protocolManager;
        this.idGeneratorManager = idGeneratorManager;
    }

    // Temporarily overpassed the raft operations interface
    // {
    //      "groupId": "xxx",
    //      "command": "transferLeader or doSnapshot or resetRaftCluster or removePeer"
    //      "value": "ip:{raft_port}"
    // }

    @PostMapping(value = "/raft")
    public RestResult<String> raftOps(@RequestBody Map<String, String> commands) {
        return protocolManager.getCpProtocol().execute(commands);
    }

    @GetMapping(value = "/idInfo")
    public RestResult<Map<String, Map<Object, Object>>> idInfo() {
        Map<String, Map<Object, Object>> info = new HashMap<>(10);
        Map<String, IdGenerator> map = idGeneratorManager.getGeneratorMap();
        map.forEach((s, idGenerator) -> info.put(s, idGenerator.info()));
        return RestResultUtils.success(info);
    }
}
