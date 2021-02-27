package tigateway;

import java.io.IOException;

import tigateway.peripheral.TiLED;
import tigateway.peripheral.WatchDog;
import tigateway.serialport.TiSerialPort;

/**
 * TiGW200 Cat1 可编程网关 支持 网络： LTE Cat1 端口： 双RS485 3个LED灯： 蓝色灯 绿色灯 红色电源灯
 * 
 * @author lemon
 *
 */
public class TiGW200 {

	private TiSerialPort[] rs485chn = new TiSerialPort[2];
	private TiLED blueLED = new TiLED(0);
	private TiLED greenLED = new TiLED(1);

	private WatchDog wdt = new WatchDog();

	private static TiGW200 instance;

	private TiGW200() {
		wdt.init();
	}

	public static TiGW200 getInstance() {
		if (instance == null) {
			instance = new TiGW200();
		}

		return instance;
	}

	/**
	 * 获取第1个通道RS485端口, 用于与其它型号产品兼容
	 * 
	 * @param baudRate   波特率
	 * @param dataBitNum 数据位
	 * @param stopBitNum 停止位
	 * @param parity     校验位 0 - 无校验 1 - 奇校验 2 - 偶校验
	 * @return
	 * @throws IOException
	 */
	public TiSerialPort getRS485(int baudRate, int dataBitNum, int stopBitNum, int parity) throws IOException {
		return this.getRS485ById(0, baudRate, dataBitNum, stopBitNum, parity);
	}

	/**
	 * 获取指定通道RS485端口, 多通道485时请使用此接口
	 * 
	 * @param id        通道 0或通道 1
	 * @param baudRate   波特率
	 * @param dataBitNum 数据位
	 * @param stopBitNum 停止位
	 * @param parity     校验位 0 - 无校验 1 - 奇校验 2 - 偶校验
	 * @return
	 * @throws IOException
	 */
	public TiSerialPort getRS485ById(int id, int baudRate, int dataBitNum, int stopBitNum, int parity)
			throws IOException {
		if (id < 0 || id > 1) {
			return null;
		}

		if (rs485chn[id] == null) {
			int uartId = 4;
			if (id == 1) {
				uartId = 5;
			}

			TiSerialPort rs485 = new TiSerialPort(uartId);
			rs485.open(baudRate, dataBitNum, stopBitNum, parity);
			rs485chn[id] = rs485;
		}

		return rs485chn[id];
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
