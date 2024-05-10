/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.codec.tlv;

import java.util.Set;
import java.util.TreeSet;

import uz.yt.ofd.android.lib.codec.tlv.exception.DuplicateTagFoundException;

public class SingleTagReader {
    private Set<Byte> singleTags = new TreeSet();
    
    public void read(TV tv, Callback callback) throws DuplicateTagFoundException, Exception {
        if (singleTags.contains(tv.getTag())) {
            throw new DuplicateTagFoundException(tv.getTag());
        }
        if(callback.assign(tv)){
            singleTags.add(tv.getTag());
        }
    }
    
    public static interface Callback {
        public boolean assign(TV tv) throws Exception;
    }
}
