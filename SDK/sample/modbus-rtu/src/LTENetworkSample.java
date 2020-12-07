import java.io.IOException;

import tigateway.TiGW200;
import tijos.framework.platform.TiPower;
import tijos.framework.platform.lpwan.lte.ILTEEventListener;
import tijos.framework.platform.lpwan.lte.TiLTE;
import tijos.framework.util.Delay;

/**
 * 4G 连接事件
 * @author Administrator
 *
 */
class LTEEventListener implements ILTEEventListener {

	/**
	 * 基站连接成功
	 */
	@Override
	public void onConnected() {
		// TODO Auto-generated method stub

	}

	/**
	 * 连接断开 
	 * @param reason 断开原因
	 */
	@Override
	public void onDisconnected(int reason) {
		
		//重新注网连接
		try {
			TiLTE.getInstance().startup(30, new LTEEventListener());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//或重启设备
//		try {
//			TiPower.getInstance().reboot(0);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}

}

/**
 * LTE 4G 网络例程
 * @author Administrator
 *
 */
public class LTENetworkSample {

	public static void main(String[] args) {

			// 获取TiGW200对象并启动看门狗
			TiGW200 gw200 = TiGW200.getInstance();
			
			try {
			// 启动4G网络,30秒超时, startup执行完成即连接成功，如果连接失败将通过IOException抛出异常
			// 网络事件通过事件通知
			TiLTE.getInstance().startup(30, new LTEEventListener());
			}
			catch(IOException ex)
			{
				System.out.println("Failed to register LTE network. " + ex.getMessage());
				ex.printStackTrace();
				return ;
			}

			// 注网成功 蓝灯亮
			gw200.blueLED().turnOn();

			try {
			// 4G设备唯一ID
			System.out.println("IMEI " + TiLTE.getInstance().getIMEI());

			// 4G信号强度
			System.out.println("RSSI " + TiLTE.getInstance().getRSSI());

			// SIM卡IMSI
			System.out.println("IMSI " + TiLTE.getInstance().getIMSI());

			// SIM卡ICCID编号
			System.out.println("ICCID " + TiLTE.getInstance().getICCID());
			}
			catch(IOException ex)
			{
				ex.printStackTrace();
			}

			// 注网成功 蓝灯亮
			gw200.blueLED().turnOff();
			
			Delay.msDelay(10000);

			System.out.println("Exiting ...");


	}

}
