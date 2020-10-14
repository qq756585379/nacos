
package com.alibaba.nacos.core.controller;

import com.alibaba.nacos.common.model.RestResult;
import com.alibaba.nacos.common.model.RestResultUtils;
import com.alibaba.nacos.core.distributed.ProtocolManager;
import com.alibaba.nacos.core.distributed.id.IdGeneratorManager;
import com.alibaba.nacos.core.utils.Commons;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(Commons.NACOS_CORE_CONTEXT + "/ops")
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

    /**
     * Gets the current health of the ID generator.
     *
     * @return {@link RestResult}
     */
    @GetMapping(value = "/idInfo")
    public RestResult<Map<String, Map<Object, Object>>> idInfo() {
        Map<String, Map<Object, Object>> info = new HashMap<>(10);
        idGeneratorManager.getGeneratorMap().forEach((resource, idGenerator) -> info.put(resource, idGenerator.info()));
        return RestResultUtils.success(info);
    }
}
