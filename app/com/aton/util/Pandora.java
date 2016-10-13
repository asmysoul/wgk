package com.aton.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 分布式唯一ID生成器，源自snowflake项目的scala版本.
 * 
 * @author youblade
 * @since v0.4
 * @created 2013-12-7 下午3:22:25
 */
public class Pandora {

	public static final Logger log = LoggerFactory.getLogger(Pandora.class);
	
	private static final Pandora INSTANCE = new Pandora(0,0);

	private long workerId;
	private long dataCenterId;
	private long sequence = 0L;

	private long twepoch = 1288834974657L;

	private long workerIdBits = 5L;
	private long datacenterIdBits = 5L;
	private long maxWorkerId = -1L ^ (-1L << workerIdBits);
	private long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
	private long sequenceBits = 12L;

	private long workerIdShift = sequenceBits;
	private long datacenterIdShift = sequenceBits + workerIdBits;
	private long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
	private long sequenceMask = -1L ^ (-1L << sequenceBits);

	private long lastTimestamp = -1L;

	private Pandora(long workerId, long dataCenterId) {
		// sanity check for workerId
		if (workerId > maxWorkerId || workerId < 0) {
			throw new IllegalArgumentException(String.format("worker Id can't be greater than {} or less than 0",
					maxWorkerId));
		}
		if (dataCenterId > maxDatacenterId || dataCenterId < 0) {
			throw new IllegalArgumentException(String.format("datacenter Id can't be greater than {} or less than 0",
					maxDatacenterId));
		}
		this.workerId = workerId;
		this.dataCenterId = dataCenterId;
		log.info("worker starting. timestamp left shift {}, datacenter id bits {}, worker id bits {}, sequence bits {}, workerid {}",
						new Object[]{timestampLeftShift, datacenterIdBits, workerIdBits, sequenceBits, workerId});
	}
	
	public static Pandora getInstance(){
		return INSTANCE;
	}
	public static Pandora newInstance(long workerId, long dataCenterId){
		return new Pandora(workerId, dataCenterId);
	}
	
	public synchronized long nextId() {
		long timestamp = timeGen();
		if (timestamp < lastTimestamp) {
			log.error("clock is moving backwards.  Rejecting requests until {}.", lastTimestamp);
			throw new RuntimeException(String.format(
					"Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
		}

		if (lastTimestamp == timestamp) {
			sequence = (sequence + 1) & sequenceMask;
			if (sequence == 0) {
				timestamp = tilNextMillis(lastTimestamp);
			}
		} else {
			sequence = 0L;
		}

		lastTimestamp = timestamp;

		return ((timestamp - twepoch) << timestampLeftShift) | (dataCenterId << datacenterIdShift)
				| (workerId << workerIdShift) | sequence;
	}

	protected static long tilNextMillis(long lastTimestamp) {
		long timestamp = timeGen();
		while (timestamp <= lastTimestamp) {
			timestamp = timeGen();
		}
		return timestamp;
	}

	protected static long timeGen() {
		return System.currentTimeMillis();
	}

}
