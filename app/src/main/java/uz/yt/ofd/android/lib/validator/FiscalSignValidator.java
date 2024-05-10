/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.validator;

import java.io.IOException;
import java.util.Date;

public interface FiscalSignValidator {

    public boolean check(String terminalID, byte[] receiptSeqRaw, byte[] dateTimeRaw, byte[] fiscalSignRaw) throws IOException;

}
