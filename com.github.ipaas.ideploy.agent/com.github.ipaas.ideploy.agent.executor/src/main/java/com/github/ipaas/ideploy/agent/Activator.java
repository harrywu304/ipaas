/**
 * Licensed under the Apache License, Version 2.0 (the "License");  +
 * * you may not use this file except in compliance with the License.
 * * You may obtain a copy of the License at  *    *
 * http://www.apache.org/licenses/LICENSE-2.0  *
 * * Unless required by applicable law or agreed to in writing, software
 * * distributed under the License is distributed on an "AS IS" BASIS,
 * * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * * See the License for the specific language governing permissions and  * limitations under the License..
 */

package com.github.ipaas.ideploy.agent;

import com.github.ipaas.ifw.mq.MqServiceManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ifw.core.service.ServiceFactory;
import com.github.ipaas.ifw.mq.Message;
import com.github.ipaas.ifw.mq.MessageHandler;
import com.github.ipaas.ifw.mq.MqListenService;
import com.github.ipaas.ifw.util.IPUtil;
import com.github.ipaas.ifw.util.JsonUtil;

/**
 * CRS agent启动类
 *
 * @author wudg
 */

public class Activator implements BundleActivator {

    private static Logger logger = LoggerFactory.getLogger(Activator.class);

    private MqListenService mqListenService;

    private static class MessageHandlerImpl implements MessageHandler {

        private BundleContext bundleContext;

        public MessageHandlerImpl(BundleContext bundleContext) {
            this.bundleContext = bundleContext;
        }

        @Override
        public void handle(Message arg0) {
            Object obj = arg0.getContent();
            logger.error(String.valueOf(obj));
            AgentCmd agentCmd = JsonUtil.toBean(String.valueOf(obj), AgentCmd.class);
            String cmd = agentCmd.getCmd();
            CrsCmdHandlerWithFeed taskHandler = CrsCmdHandlerFactory.getCmdHandler(cmd);
            if (taskHandler != null) {
                taskHandler.executeCmd(agentCmd, bundleContext);
            }
        }
    }

    public void run(BundleContext bundleContext) throws Exception {
        String localIp = IPUtil.getLocalIP(true);
        logger.info("localIp:" + localIp);
        mqListenService = MqServiceManager.getMqListenService(Constants.CRS_APP_ID);
        mqListenService.listenQueue(Constants.AGENT_CTRL_QUEUE + localIp, new MessageHandlerImpl(bundleContext));
    }

    @Override
    public void start(BundleContext cxt) throws Exception {
        logger.info("CRS Agent正在启动");
        try {
            run(cxt);
        } catch (Exception e) {
            logger.error("CRS启动失败", e);
            throw e;
        }

        logger.info("CRS Agent已经启动");
    }

    @Override
    public void stop(BundleContext cxt) throws Exception {
        if (mqListenService != null) {
            //mqListenService.releaseResource();
        }
        logger.info("CRS Agent已经停止");
    }

}
