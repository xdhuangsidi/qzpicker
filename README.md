# qzpicker  七政四余静盘计算安卓ａｐｐ，ｂｕｇ及改进建议欢迎在上面的Issues提交
<img src="https://github.com/xdhuangsidi/qzpicker/blob/master/spanshot.png"   />
下载地址　　https://raw.githubusercontent.com/xdhuangsidi/qzpicker/master/bin/qzpicker.apk　，本ａｐｐ对ｍｏｉｒａ进行了精简封装，仅保留排静盘的功能，对于动盘，可以使用一些天文观星软件了查看日月五星的地平方位　比如 http://shouji.baidu.com/game/11430023.html  。
由于只是做了封装，为了简单起见　　图形界面以文本为主　。具体计算代码在　https://github.com/xdhuangsidi/qzpicker/blob/master/src/com/example/qzpicker/MainActivity.java　　的９０－１３４行　。静盘行星移动速度较慢，除了月球有０．５度的误差外，其他星的误差可以忽略不计而且基本不随地方时变化，所以本ａｐｐ以简单起见，没有加入经纬度。对于星的运算，要提前输入时间参数，即date_buf数组，分别储存　年　月　日　时　分。　然后 cal.initSpecial(birth_sign_pos_sun, birth_sign_pos_MOON, day_time);初始化日月的位置信息以进行行星位置状态计算，得出逆顺留迟的状态。
然后　pos = cal.compute(i);ｐｏｓ为行星位置　　ｉ为行星代号０为太阳　１月亮　　２水星　３金星　４火星　５木星　６　土星　７８９为天海冥王星　１１为罗睺星位置，其ｐｏｓ＋１８０为计都位置。　　１２为月孛　　紫气代表的是某种周期，所以用这两行代码计算
　　　　cal.setOrbitData(0.035200321903032655, Calculate.getJulianDayUT(new int[] {1975,3,13,16,0}), 230.5);
			　pos = cal.compute(-1);
计算之后用　ｆｏｒｍａｔｅ（）方法把ｐｏｓ转化成具体名称　
