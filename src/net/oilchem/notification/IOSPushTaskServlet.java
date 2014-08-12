package net.oilchem.notification;

import net.oilchem.common.bean.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 13-4-17
 * Time: 上午9:33
 * To change this template use File | Settings | File Templates.
 */
public class IOSPushTaskServlet extends HttpServlet {

    Logger logger = LoggerFactory.getLogger(IOSPushTaskServlet.class);

    NotificationRepository notificationRepository;
    public IOSPushTaskServlet() {
        notificationRepository = getCurrentWebApplicationContext().getBean(NotificationRepository.class);
    }

    @Override
    public void init() throws ServletException {
        logger.info("TaskServlet contextInitialized invoke........");

        if(Config.iOSPushSwitchOpen){
            //10秒钟后开始执行任务
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    notificationRepository.runTask();
                }
            },10000);
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("TaskServlet doGet invoke ==========");
        req.getRequestDispatcher("/index.jsp").forward(req,resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req,resp);
    }

    @Override
    public void destroy() {
        notificationRepository.stopTask();
    }
}
