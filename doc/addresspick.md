## 地址联动控件AddressPicker


* **AddressPicker**
  
 
   ![demo5](https://raw.githubusercontent.com/Callanna/CustomView/master/art/demo5.gif) 

## Usage

You can create your own progress wheel in xml like this (remeber to add ```xmlns:wheel="http://schemas.android.com/apk/res-auto"```):

```xml
 <com.cvlib.address.AddressPicker
        android:id="@+id/address"
        android:layout_width="match_parent"
        android:layout_height="300dp"
        app:aItemNumber="5"
        app:aLineColor="#ff5789DC"
        app:aMaskHeight="32dp"
        app:aNormalTextColor="#885789DC"
        app:aNormalTextSize="14sp"
        app:aSelectedTextColor="#ff5789DC"
        app:aSelectedTextSize="22sp"
        app:aUnitHeight="40dp">

    </com.cvlib.address.AddressPicker>
```

in code:

```Java
AddressPicker addressPicker =  ((AddressPicker)findViewById(R.id.address));

//地址变化监听回调
 addressPicker.setOnAddressPickListener(new AddressPicker.OnAddressPickListener() {
            @Override
            public void onAddressPicked(final String province, final String city, final String county) {
                Log.d("duanyl", "onAddressPicked: "+province+city+county);
                //TODO 
              }   
            }
        });
  //设置地址 
  addressPicker.setProvince("浙江");
  addressPicker.setCity("杭州");
  addressPicker.setRegion(“西湖区”);

  //获取当前地址信息
  addressPicker.getCurrentAddress()
...

```
 
 
 
 
