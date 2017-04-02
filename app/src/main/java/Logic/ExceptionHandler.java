package Logic;

import DB.FireBaseOperator;

/**
 * Created by Eilon Laor & Dvir Twina on 06/02/2017.
 *
 * The ExceptionHandler records on the dataBase every exception
 *
 */

public class ExceptionHandler {
    public ExceptionHandler(Exception e, FireBaseOperator fireBaseOperator) {
        fireBaseOperator.InsertErrorLog( e.getStackTrace()[0].getClassName()+"/"+e.getStackTrace()[0].getMethodName()+" : "+e.getStackTrace()[0].getLineNumber());
        e.printStackTrace();
        fireBaseOperator.GetActivity().finish();
    }
}
