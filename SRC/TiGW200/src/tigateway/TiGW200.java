package tigateway;

import java.io.IOException;

import tigateway.peripheral.TiLED;
import tigateway.peripheral.TiRS485;
import tigateway.peripheral.WatchDog;

/**
 * TiGW200 Cat1 可编程网关 支持 网络： LTE Cat1 端口： 双RS485 3个LED灯： 蓝色灯 绿色灯 红色电源灯
 * 
 * @author lemon
 *
 */
public class TiGW200 {

	private TiRS485[] rs485chn = new TiRS485[2];
	private TiLED blueLED = new TiLED(0);
	private TiLED greenLED = new TiLED(1);

	private WatchDog wdt = new WatchDog();

	private static TiGW200 instance ;
	
	private TiGW200()
	{
		wdt.init();
	}
	
	public static TiGW200 getInstance()
	{
		if(instance == null)
		{
			instance = new TiGW200();
		}
		
		return instance;
	}
	
	/**
	 * 获取RS485端口 数据位8 停止位1
	 * 
	 * @param chn      通道 0或通道 1
	 * @param baudRate 波特率
	 * @param parity   校验位 0 - 无校验 1 - 奇校验 2 - 偶校验
	 * @return
	 * @throws IOException
	 */
	public TiRS485 getRS485(int chn, int baudRate, int parity) throws IOException {
		if (chn < 0 || chn > 1) {
			return null;
		}

		if (rs485chn[chn] == null) {
			TiRS485 rs485 = new TiRS485(chn);
			rs485.open(baudRate, parity);

			rs485chn[chn] = rs485;
		}

		return rs485chn[chn];
	}

	/**
	 * 蓝色灯
	 * 
	 * @return
	 */
	public TiLED blueLED() {
		return blueLED;
	}

	/**
	 * 绿色灯
	 * 
	 * @return
	 */
	public TiLED greenLED() {
		return greenLED;
	}

}
