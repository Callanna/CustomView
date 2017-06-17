package com.callanna.viewlibrary.address;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.callanna.viewlibrary.R;
import com.callanna.viewlibrary.WheelView;

import java.util.List;

/**
 * Created by Callanna on 2017/6/14.
 */

public class AddressPicker extends LinearLayout {
    /**
     * 线的默认颜色
     */
    private int lineColor = 0xff000000;
    /**
     * 线的默认宽度
     */
    private float lineHeight = 2f;
    /**
     * 默认字体
     */
    private float normalFont = 14.0f;
    /**
     * 选中的时候字体
     */
    private float selectedFont = 22.0f;
    /**
     * 单元格高度
     */
    private int unitHeight = 50;
    /**
     * 显示多少个内容
     */
    private int itemNumber = 7;
    /**
     * 默认字体颜色
     */
    private int normalColor = 0xff000000;
    /**
     * 选中时候的字体颜色
     */
    private int selectedColor = 0xffff0000;
    /**
     * 蒙板高度
     */
    private float maskHeight = 48.0f;

    private String defaultProvince = "", defaultCity = "", defaultRegion = "";

    private WheelView picker_provice;
    private WheelView picker_city;
    private WheelView picker_region;

    private String currentAddress;

    private Context context;

    public AddressPicker(Context context) {
        this(context, null);
    }

    public AddressPicker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AddressPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.layout_window_address, this);
        picker_provice = (WheelView) findViewById(R.id.wh_province);
        picker_city = (WheelView) findViewById(R.id.wh_city);
        picker_region = (WheelView) findViewById(R.id.wh_region);
        initView(context, attrs);
        initData();
        initEvent();
    }

    private void initEvent() {

        picker_provice.setOnSelectListener(new WheelView.OnSelectListener() {
            @Override
            public void endSelect(int id, String text) {
                List<String> city = AreaFilterTask.getInstance(context).getCity(text);
                if (city.size() > 1) {
                    picker_city.setData(city);
                    picker_city.setDefault(0);
                    List<String> region = AreaFilterTask.getInstance(context).getRegion(city.get(0).toString());
                    if (region.size() > 0) {
                        picker_region.setData(region);
                        picker_region.setDefault(0);
                    } else {
                        picker_region.clearData();
                    }
                }
                if (onAddressPickListener != null) {
                    onAddressPickListener.onAddressPicked(picker_provice.getSelectedText(),
                            picker_city.getSelectedText(),
                            picker_region.getSelectedText());
                }
            }

            @Override
            public void selecting(int id, String text) {
            }
        });
        picker_city.setOnSelectListener(new WheelView.OnSelectListener() {
            @Override
            public void endSelect(int id, String text) {
                List<String> region = AreaFilterTask.getInstance(context).getRegion(text);
                if (region.size() > 0) {
                    picker_region.setData(region);
                    picker_region.setDefault(0);
                }
                if (onAddressPickListener != null) {
                    onAddressPickListener.onAddressPicked(picker_provice.getSelectedText(),
                            picker_city.getSelectedText(),
                            picker_region.getSelectedText());
                }
            }

            @Override
            public void selecting(int id, String text) {
            }
        });
        picker_region.setOnSelectListener(new WheelView.OnSelectListener() {
            @Override
            public void endSelect(int id, String text) {
                if (onAddressPickListener != null) {
                    onAddressPickListener.onAddressPicked(picker_provice.getSelectedText(),
                            picker_city.getSelectedText(),
                            picker_region.getSelectedText());
                }
            }

            @Override
            public void selecting(int id, String text) {

            }
        });
    }

    public String getCurrentAddress() {
        currentAddress = picker_provice.getSelectedText() + picker_city.getSelectedText() + picker_region.getSelectedText();
        return currentAddress;
    }

    private void initData() {
        AreaFilterTask.getInstance(context).filterArea(new AreaFilterTask.LoadedDataCallBack() {
            @Override
            public void initData(List<Province> data) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        initCity();
                    }
                });
            }
        });
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initCity();
    }


    private void initCity() {
            picker_provice.setData(AreaFilterTask.getInstance(context).getProvince());
            picker_provice.setDefault(defaultCity);
            List<String> citys = AreaFilterTask.getInstance(context).getCity(picker_provice.getSelectedText());
            if (citys.size() > 0) {
                picker_city.setData(citys);
                picker_city.setDefault(defaultCity);
                List<String> region = AreaFilterTask.getInstance(context).getRegion(picker_city.getSelectedText());
                if (region.size() > 0) {
                    picker_region.setData(region);
                    picker_region.setDefault(defaultRegion);
                } else {
                    picker_region.clearData();
                }
            }
    }

    /**
     * 初始化，获取设置的属性
     *
     * @param context
     * @param attrs
     */
    private void initView(Context context, AttributeSet attrs) {
        TypedArray attribute = context.obtainStyledAttributes(attrs, R.styleable.AddressPicker);
        unitHeight = (int) attribute.getDimension(R.styleable.AddressPicker_aUnitHeight, unitHeight);
        itemNumber = attribute.getInt(R.styleable.AddressPicker_aItemNumber, itemNumber);

        normalFont = attribute.getDimension(R.styleable.AddressPicker_aNormalTextSize, normalFont);
        selectedFont = attribute.getDimension(R.styleable.AddressPicker_aSelectedTextSize, selectedFont);
        normalColor = attribute.getColor(R.styleable.AddressPicker_aNormalTextColor, normalColor);
        selectedColor = attribute.getColor(R.styleable.AddressPicker_aSelectedTextColor, selectedColor);

        lineColor = attribute.getColor(R.styleable.AddressPicker_aLineColor, lineColor);
        lineHeight = attribute.getDimension(R.styleable.AddressPicker_aLineHeight, lineHeight);

        maskHeight = attribute.getDimension(R.styleable.AddressPicker_aMaskHeight, maskHeight);
        attribute.recycle();
        picker_provice.setUnitHeight(unitHeight);
        picker_provice.setItemNumber(itemNumber);
        picker_provice.setNormalColor(normalColor);
        picker_provice.setNormalFont(normalFont);
        picker_provice.setSelectedColor(selectedColor);
        picker_provice.setSelectedFont(selectedFont);
        picker_provice.setLineColor(lineColor);
        picker_provice.setLineHeight(lineHeight);
        picker_provice.setMaskHeight(maskHeight);

        picker_city.setUnitHeight(unitHeight);
        picker_city.setItemNumber(itemNumber);
        picker_city.setNormalColor(normalColor);
        picker_city.setNormalFont(normalFont);
        picker_city.setSelectedColor(selectedColor);
        picker_city.setSelectedFont(selectedFont);
        picker_city.setLineColor(lineColor);
        picker_city.setLineHeight(lineHeight);
        picker_city.setMaskHeight(maskHeight);

        picker_region.setUnitHeight(unitHeight);
        picker_region.setItemNumber(itemNumber);
        picker_region.setNormalColor(normalColor);
        picker_region.setNormalFont(normalFont);
        picker_region.setSelectedColor(selectedColor);
        picker_region.setSelectedFont(selectedFont);
        picker_region.setLineColor(lineColor);
        picker_region.setLineHeight(lineHeight);
        picker_region.setMaskHeight(maskHeight);
    }

    public void setProvince(final String province) {
        defaultProvince = province;
        post(new Runnable() {
            @Override
            public void run() {
                picker_provice.setData(AreaFilterTask.getInstance(context).getProvince());
                picker_provice.setDefault(province);
                List<String> citys = AreaFilterTask.getInstance(context).getCity(picker_provice.getSelectedText());
                if (citys.size() > 0) {
                    picker_city.setData(citys);
                    picker_city.setDefault(defaultCity);
                    List<String> region = AreaFilterTask.getInstance(context).getRegion(picker_city.getSelectedText());
                    if (region.size() > 0) {
                        picker_region.setData(region);
                        picker_region.setDefault(defaultRegion);
                    } else {
                        picker_region.clearData();
                    }
                }
            }
        });
    }

    public void setCity(final String city) {
        defaultCity = city;
        post(new Runnable() {
            @Override
            public void run() {
                picker_city.setDefault(city);
                List<String> region = AreaFilterTask.getInstance(context).getRegion(picker_city.getSelectedText());
                if (region.size() > 0) {
                    picker_region.setData(region);
                    picker_region.setDefault(defaultRegion);
                } else {
                    picker_region.clearData();
                }
            }
        });
    }

    public void setRegion(final String region) {
        defaultRegion = region;
        post(new Runnable() {
            @Override
            public void run() {
                picker_region.setDefault(region);
            }
        });
    }

    public String getRegion() {
        return picker_region.getSelectedText();
    }

    public String getCity() {
        return picker_city.getSelectedText();
    }

    public String getProvice() {
        return picker_provice.getSelectedText();
    }

    private OnAddressPickListener onAddressPickListener;

    public void setOnAddressPickListener(OnAddressPickListener onAddressPickListener) {
        this.onAddressPickListener = onAddressPickListener;
    }

    /**
     * The interface On address pick listener.
     */
    public interface OnAddressPickListener {
        /**
         * On address picked.
         *
         * @param province the province
         * @param city     the city
         * @param region   the county ，if {@hideCounty} is true，this is null
         */
        void onAddressPicked(String province, String city, String region);
    }


}
