package tigateway.peripheral;

import java.io.IOException;

/**
 * RS485接口
 *
 * @author TiJOS
 */
public class TiRS485 {

	private int channel;

	/**
	 * RS485
	 * 
	 * @param chn 通道号
	 * @throws IOException
	 */
	public TiRS485(int chn) throws IOException {
		this.channel = chn;
	}

	/**
	 * Open with communication parameters
	 *
	 * @param baudRate
	 * @param parity
	 * @throws IOException
	 */

	/**
	 * 通讯参数
	 * 
	 * @param baudRate 波特率
	 * @param parity   校验位 0 无校验 1奇校验 2偶校验
	 * @throws IOException
	 */
	public void open(int baudRate, int parity) throws IOException {

		Transmiter.getInstance().openChannel(this.channel, baudRate, parity);
	}

	/**
	 * Close
	 *
	 * @throws IOException
	 */
	public void close() throws IOException {
	}

	/**
	 * 下发数据 由于485是半双工, 下发时需要提供下发命令后最大接收数据长度和超时时间
	 * 
	 * @param request     下发数据
	 * @param reqLen      数据长度
	 * @param recvLen     期望接收长度
	 * @param recvTimeout 期望接收超时 毫秒
	 * @throws IOException
	 */
	public void write(byte[] request, int reqLen, int recvLen, int recvTimeout) throws IOException {
		Transmiter.getInstance().write(this.channel, request, reqLen, recvLen, recvTimeout);
	}

	/**
	 * 读取回复数据
	 * 
	 * @param timeout
	 * @return
	 * @throws IOException
	 */
	public byte[] read(int timeout) throws IOException {
		// 读取长度
		int recvLen = this.readRecvLen(timeout);
		if (recvLen == 0) {
			return null;
		}

		// 读取数据
		return this.readData(recvLen, timeout);
	}

	/**
	 * 读取接收到的数据长度 获取长度后可通过readData读取所收到的数据 readRecvLen与readData需配合使用
	 * 
	 * @param timeout
	 * @return
	 * @throws IOException
	 */
	public int readRecvLen(int timeout) throws IOException {
		return Transmiter.getInstance().readLen(timeout);
	}

	/**
	 * 读取接收到的数据
	 * 
	 * @param readLen
	 * @param timeout
	 * @return
	 * @throws IOException
	 */
	public byte[] readData(int readLen, int timeout) throws IOException {
		return Transmiter.getInstance().readData(readLen, timeout);
	}

	/**
	 * 读取指定长度的数据到缓存区, readRecvLen 结合使用
	 * 
	 * @param buffer  缓存区
	 * @param offset  缓存区偏移
	 * @param readLen 读取长度
	 * @param timeout 超时
	 * @return
	 * @throws IOException
	 */
	public int readData(byte[] buffer, int offset, int readLen, int timeout) throws IOException {
		return Transmiter.getInstance().readData(buffer, offset, readLen, timeout);
	}

	/*
	 * 下发命令并接收数据
	 */
	public byte[] deviceIO(byte[] request, int expRecvLen, int recvTimeout) throws IOException {
		return Transmiter.getInstance().deviceIO(this.channel, request, expRecvLen, recvTimeout);
	}

}
