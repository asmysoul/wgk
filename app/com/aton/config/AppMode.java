package com.aton.config;

import java.util.List;
import java.util.Map;

import play.Play;
import play.test.Fixtures;

import com.google.common.collect.Maps;

// @formattor:off
/**
 * 应用程序的Mode：结合Play.Mode进行了扩展，与conf配置中的app.mode、app.mode.yml两个参数配合使用<br>
 * 
 * 【设计用途】
 * 便于开发时切换场景，前后端配合开发时使用mock数据避免前端开发进度阻塞，后端开发时跳过某些正常流程等.<br>
 * 
 * 【特点】
 *	1、AppMode实例由一个标识和一组开关属性组成，标识用于区分不同的实例、标记应用运行的场景，开关用于决定代码中是否执行某段特定的逻辑
 *    （如：mock数据、跳过实际的短信发送流程等）<br>
 *  2、应用运行时可使用的AppMode实例，配置在conf/init-appmode.yml文件中<br>
 * 	3、应用每次启动时，都对应一个AppMode实例（即运行场景），该实例参数app.mode配置在conf文件中，对应实例集合中配置的某个实例<br>
 *  
 * 【用法】
 *  1、在conf配置文件中，app.mode.yml指定从哪里读取默认的AppMode实例集合，app.mode指定当前使用哪个AppMode实例
 *  2、在具体开发中，可在init-appmode.yml中添加新的AppMode，也可自定义类似文件（要修改app.mode.yml参数）新配置一个AppMode实例，
 *     采用何种方式具体取决于是否需要与他人共享AppMode实例配置
 *  3、在测试类代码中可使用{@link #switchMode(Mode)}方法随时切换AppMode，来测试不同的逻辑
 * 
 * @author youblade
 * @since  v0.1
 * @created 2014年7月7日 下午5:07:27
 */
// @formattor:on
public class AppMode {

	/**
	 * AppMode实例的标识，值为{@link Mode#toString()}
	 */
	public Mode mode;
	
	/**
	 * 是否为前端开发构造假数据
	 */
	public boolean mockFrontend;
	
	/**
	 * 是否禁用Job
	 */
	public boolean disableJob;
	
	/**
	 * 是否为模拟财付通接口
	 */
	public boolean mockTenpay;
	
	/**
	 * 测试支付接口
	 */
	public boolean testPay;
	
	/**
	 * 是否禁用会员功能限制的检查<br>
	 * 1、开发时默认为true
	 * 2、上线后默认为fase：非付费会员不能使用大部分功能
	 */
	public boolean disableMemberCheck;
	
	public enum Mode{
		ONLINE,DEV,TEST
	}
	
	private static String configMode = Config.getProperty("app.mode", "DEV");
	private static Map<String, AppMode> modes = Maps.newHashMapWithExpectedSize(5);

	/**
	 * 
	 * 获取当前系统配置的AppMode.
	 *
	 * @return
	 * @since  v0.1
	 * @author youblade
	 * @created 2014年7月7日 下午5:05:11
	 */
	public static AppMode get() {
		// init AppMode config
		if (modes.isEmpty()) {
			List<AppMode> list = (List<AppMode>) Fixtures.loadYamlAsList(Config.APP_MODE_YML);
			for (AppMode appMode : list) {
				modes.put(appMode.mode.toString(), appMode);
			}
		}
		return modes.get(configMode);
	}

	public boolean isOnline(){
		return Play.mode.isProd() && Mode.ONLINE.toString().equals(configMode);
	}
	public boolean isNotOnline(){
		return !isOnline();
	}
	
	public static void switchMode(Mode dev){
		configMode = dev.toString();
	}
}
