/*
 *  Copyright 2011 Yuri Kanivets
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
 */
package im.boss66.com.widget.wheel;



import android.content.Context;

/**
 * The simple Array wheel adapter
 * @param <T> the element type
 */
public class ArrayWheelAdapter<T> extends AbstractWheelTextAdapter {

    // items
    private T items[];
    private String[] listBank;
    /**
     * Constructor
     * @param context the current context
     */
//    public ArrayWheelAdapter(Context context, T items[]) {
//        super(context);
//
//        //setEmptyItemResource(TEXT_VIEW_ITEM_RESOURCE);
//        this.items = items;
//    }

    public ArrayWheelAdapter(Context context,
			String[] listBank) {
    	super(context);
    	this.listBank = listBank;
	}

	@Override
    public CharSequence getItemText(int index) {
		if (items != null && items.length > 0) {
			if (index >= 0 && index < items.length) {
	            T item = items[index];
	            if (item instanceof CharSequence) {
	                return (CharSequence) item;
	            }
	            return item.toString();
	        }
		}else if(listBank != null && listBank.length> 0){
			return listBank[index];
		}

        return null;
    }

    @Override
    public int getItemsCount() {
		int size = 0;
    	if (items != null && items.length > 0) {
			size = items.length;
		}else if(listBank != null && listBank.length > 0){
			size = listBank.length;
		}
        return size;
    }
}
