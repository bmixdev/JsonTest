/******************************************************************************
 *
 *  2016 (C) Copyright Open-RnD Sp. z o.o.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 ******************************************************************************/

package ru.bmixsoft.jsontest.multiview.data;
import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;

public class DataProvider {

     private static DataProvider instance;
    private Context context;
    private ArrayList<HashMap<String, Object>> lstData;
    public static final String KEY_PARENT_NODE = "PARENT_NODE";
    public static final String KEY_PARENT_GROUP = "IS_GROUP";



    public DataProvider(Context context, ArrayList<HashMap<String, Object>> list) {
        this.context = context;
        this.lstData = list;
    }

    public static DataProvider getInstance(Context context, ArrayList<HashMap<String, Object>> list) {
           /*
            synchronized (DataProvider.class) {
                if (instance == null) {
                    instance = new DataProvider(context, slidemenu);
                }
                else{
                    if(! slidemenu.equals(instance.lstData))
                    {
                        instance.lstData.clear();
                    }
                }
            }
            */
        instance = new DataProvider(context, list);
        return instance;
    }


    public  ArrayList<HashMap<String, Object>> getInitialItems()
    {
       return getSubItems(null);
    }

    public ArrayList<HashMap<String, Object>> getSubItems(HashMap<String, Object> BaseMap) {
        ArrayList<HashMap<String, Object>> lst = new ArrayList<>();
        for (HashMap<String, Object> map : lstData)
        {
            if (map.get(KEY_PARENT_NODE) == BaseMap) {
                lst.add(map);
            }
        }
        return  lst;
    }

    public static boolean isExpandable(HashMap<String, Object> map) {
        return map.get(KEY_PARENT_GROUP).equals("1");
    }
}
