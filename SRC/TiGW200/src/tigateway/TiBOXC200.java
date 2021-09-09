package tigateway;

import java.io.IOException;

import tigateway.peripheral.TiLED;
import tigateway.serialport.TiSerialPort;

public class TiBOXC200 {

	static final int rs485UartId = 1;
	static final int rs485DuplexGpio = 10;

	static final int rs232UartId = 2;

	private TiSerialPort rs485 = null;
	private TiSerialPort rs232 = null;

	private TiLED blueLED = new TiLED(0);
	private TiLED greenLED = new TiLED(1);

	private static TiBOXC200 instance;

	public static TiBOXC200 getInstance() {
		if (instance == null) {
			instance = new TiBOXC200();
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

		if (rs485 == null) {
			rs485 = new TiSerialPort(rs485UartId);
			rs485.open(baudRate, dataBitNum, stopBitNum, parity);
			rs485.setRS485DuplexLine(0, rs485DuplexGpio);
		}

		return rs485;
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
	public TiSerialPort getRS232(int baudRate, int dataBitNum, int stopBitNum, int parity) throws IOException {

		if (rs232 == null) {
			rs232 = new TiSerialPort(rs232UartId);
			rs232.open(baudRate, dataBitNum, stopBitNum, parity);
		}

		return rs232;
	}

	/**
	 * 通过串口获取通道
	 * 
	 * @param id         0 RS485 1 RS232
	 * @param baudRate
	 * @param dataBitNum
	 * @param stopBitNum
	 * @param parity
	 * @return
	 * @throws IOException
	 */
	public TiSerialPort getSerialPort(int id, int baudRate, int dataBitNum, int stopBitNum, int parity)	throws IOException {
		if (id == 0) {
			return this.getRS485(baudRate, dataBitNum, stopBitNum, parity);
		} else {
			return this.getRS232(baudRate, dataBitNum, stopBitNum, parity);
		}
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
