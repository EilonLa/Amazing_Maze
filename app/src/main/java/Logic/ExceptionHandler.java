package Logic;

import DB.FireBaseOperator;

/**
 * Created by Eilon Laor & Dvir Twina on 06/02/2017.
 *
 * The ExceptionHandler records on the dataBase every exception
 *
 */

public class ExceptionHandler {
    public ExceptionHandler(String message, FireBaseOperator fireBaseOperator) {
        fireBaseOperator.InsertErrorLog(message);
        fireBaseOperator.GetActivity().finish();
    }
}
