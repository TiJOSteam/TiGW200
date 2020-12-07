package tigateway.peripheral;

import java.io.IOException;

import tijos.framework.devicecenter.TiUART;
import tijos.framework.util.Delay;
import tijos.framework.util.Formatter;
import tijos.framework.util.LittleBitConverter;
import tijos.framework.util.logging.Logger;

public class Transmiter {

	private static final int TAG_DOWNSTREAM = '@';
	private static final int UP_DOWNSTREAM = '$';

	private TiUART uart;

	static Transmiter instance;

	public static Transmiter getInstance() throws IOException {
		if (instance == null) {
			instance = new Transmiter();
			instance.init();
		}

		return instance;
	}

	/**
	 * Initialize
	 * 
	 * @throws IOException
	 */
	public void init() throws IOException {
		// RS485使用UART3 根据外设进行初始化
		this.uart = TiUART.open(3);

		// UART通讯参数
		uart.setWorkParameters(8, 1, 0, 115200);
	}

	/**
	 * Uninitailze
	 * 
	 * @throws IOException
	 */
	public void deinit() throws IOException {
		if (this.uart != null) {
			this.uart.close();
			this.uart = null;
		}
	}

	/**
	 * Open the specified channel
	 * 
	 * @param chn
	 * @param baudrate
	 * @param parity
	 * @throws IOException
	 */
	public void openChannel(int chn, int baudrate, int parity) throws IOException {
		byte[] request = setParametersRequest(chn, baudrate, parity);

		this.uart.write(request, 0, request.length);

		byte[] result = this.readData(5, 200);
		if (result == null) {
			throw new IOException("Timeout");
		}

		if (result[0] != UP_DOWNSTREAM) {
			throw new IOException("Invalid tag");
		}

		if (result[4] != 0) {
			throw new IOException("Invalid parameters");
		}
	}

	/**
	 * Send and receive
	 * 
	 * @param chn
	 * @param data
	 * @param expRecvLen
	 * @param recvTimeout
	 * @return
	 * @throws IOException
	 */
	public byte[] deviceIO(int chn, byte[] data, int expRecvLen, int recvTimeout) throws IOException {
		byte[] request = writeDataRequest(chn, data, data.length, expRecvLen, recvTimeout);
		this.uart.write(request, 0, request.length);

		this.write(chn, data, data.length, expRecvLen, recvTimeout);
		int recvLen = this.readLen(recvTimeout);
		if (recvLen == 0) {
			return null;
		}

		return this.readData(recvLen, recvTimeout);
	}

	/**
	 * 下发数据
	 * 
	 * @param chn         通道 0 或 1
	 * @param data        数据
	 * @param expRecvLen  期望接收数据长度
	 * @param recvTimeout 接收数据超时
	 * @throws IOException
	 */
	public void write(int chn, byte[] data, int length, int expRecvLen, int recvTimeout) throws IOException {

		this.uart.clear(TiUART.BUFF_READ);
		byte[] request = writeDataRequest(chn, data, length, expRecvLen, recvTimeout);
		this.uart.write(request, 0, request.length);
	}

	/**
	 * 读取接收数据长度
	 * 
	 * @param timeout
	 * @return
	 * @throws IOException
	 */
	public int readLen(int timeout) throws IOException {

		byte[] head = new byte[4];
		int len = this.readToBuffer(head, 0, 4, timeout);

		if (len < 4) {
			throw new IOException("Timeout");
		}

		if (head[0] != UP_DOWNSTREAM) {
			throw new IOException("Invalid tag");
		}

		int dataLen = LittleBitConverter.ToUInt16(head, 1);
		// no data
		if (dataLen <= 0) {
			return 0;
		}

		return dataLen - 1;
	}

	public byte[] readData(int recvLen, int timeout) throws IOException {

		byte[] payload = new byte[recvLen];
		int len = this.readToBuffer(payload, 0, recvLen, timeout);

		if (len == 0) {
			return null;
		}

		if (len < recvLen) {
			byte[] newPayload = new byte[len];
			System.arraycopy(payload, 0, newPayload, 0, len);
			return newPayload;
		}

		return payload;
	}

	public int readData(byte[] payload, int offset, int recvLen, int timeout) throws IOException {

		return this.readToBuffer(payload, offset, recvLen, timeout);
	}

	private byte[] setParametersRequest(int chn, int baudrate, int parity) {
		int chn_interval = 100; // 同1通道最小通讯间隔100ms

		int param1 = baudrate;
		int param2 = chn_interval << 8 | parity;

		int total = 12;

		int pos = 0;
		byte[] buff = new byte[total];
		buff[pos++] = TAG_DOWNSTREAM;
		LittleBitConverter.FillBytes((short) (total - 3), buff, pos);
		pos += 2;

		if (chn == 0) {
			buff[pos++] = 0;
		} else {
			buff[pos++] = (byte) 0x80;
		}
		LittleBitConverter.FillBytes(param1, buff, pos);
		pos += 4;

		LittleBitConverter.FillBytes(param2, buff, pos);
		pos += 4;

		return buff;
	}

	private byte[] writeDataRequest(int chn, byte[] data, int length, int expRecvLen, int recvTimeout) {
		int total = 12 + length;
		int pos = 0;
		byte[] buff = new byte[total];

		int param1 = expRecvLen;
		int param2 = recvTimeout;

		buff[pos++] = TAG_DOWNSTREAM;
		LittleBitConverter.FillBytes((short) (total - 3), buff, pos);
		pos += 2;

		if (chn == 0) {
			buff[pos++] = 0x01;
		} else {
			buff[pos++] = (byte) 0x81;
		}

		LittleBitConverter.FillBytes(param1, buff, pos);
		pos += 4;

		LittleBitConverter.FillBytes(param2, buff, pos);
		pos += 4;

		System.arraycopy(data, 0, buff, pos, length);

		return buff;
	}

	/**
	 * Read data into buffer from the UART
	 *
	 * @param start
	 * @param length
	 * @param modbusClient
	 * @return read length
	 * @throws IOException
	 */
	private int readToBuffer(byte[] buffer, int start, int length, int timeOut) throws IOException {

		long now = System.currentTimeMillis();
		long deadline = now + timeOut;
		int offset = start;
		int bytesToRead = length;
		int res;
		while ((now < deadline) && (bytesToRead > 0)) {
			res = this.uart.read(buffer, offset, bytesToRead);
			if (res <= 0) {
				Delay.msDelay(10);
				now = System.currentTimeMillis();
				continue;
			}

			offset += res;
			bytesToRead -= res;
			if (bytesToRead > 0) // only to avoid redundant call of System.currentTimeMillis()
				now = System.currentTimeMillis();
		}
		res = length - bytesToRead; // total bytes read
		if (res < length) {
			Logger.info("TiRS485",
					"Read timeout(incomplete): " + Formatter.toHexString(buffer, offset, start + res, ""));
		}

		return res;
	}

}
