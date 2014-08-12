package net.oilchem;

//import javapns.back.PushNotificationManager;
//import javapns.back.SSLConnectionHelper;
//import javapns.data.Device;
//import javapns.data.PayLoad;

public class MainApnsSend {

    public static void main(String[] args) throws Exception {
//        try {
//            String deviceToken = "e775b5892f3334427c14def8aa4d8189a4ec1c795020072f4baa7ee92e50b1db";//iphone手机获取的token
//
//            PayLoad payLoad = new PayLoad();
//            payLoad.addAlert("我的push测试");//push的内容
//            payLoad.addBadge(1);//图标小红圈的数值
//            payLoad.addSound("default");//铃音
//            PushNotificationManager pushManager = PushNotificationManager.getInstance();
//            pushManager.addDevice("iPhone", deviceToken);
////Connect to APNs
///************************************************测试的服务器地址：gateway.sandbox.push.apple.com /端口2195 产品推送服务器地址：gateway.push.apple.com / 2195
// ***************************************************/
//            String host = "gateway.sandbox.push.apple.com";
//            int port = 2195;
//            String certificatePath = "/Users/jcjc/Desktop/push_p.p12";//导出的证书
//            String certificatePassword = "sunlg";//此处注意导出的证书密码不能为空因为空密码会报错
//            pushManager.initializeConnection(host, port, certificatePath, certificatePassword, SSLConnectionHelper.KEYSTORE_TYPE_PKCS12);//Send Push
//            Device client = pushManager.getDevice("iPhone");
//            pushManager.sendNotification(client, payLoad);
//            pushManager.stopConnection();
//
//            pushManager.removeDevice("iPhone");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }
}

