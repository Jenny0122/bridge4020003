package kr.co.wisenut.common.Exception;
import java.sql.SQLException;

/**
 * Created by KoreaWISENut
 * User: KoreaWisenut
 * Date: 2005. 5. 16.
 */

public class DBFactoryException extends Exception{
    protected StringBuffer message = new StringBuffer();
    protected Throwable throwable = null;

    public DBFactoryException(){
    };

    public DBFactoryException(String exp){
        message.append(exp);
    }

    public DBFactoryException(Throwable throwable) {
        this("", throwable);
    }

    public DBFactoryException(String message, Throwable throwable) {
        super();
        this.message.append( message );
        this.throwable = throwable;
    }

    public synchronized void handlelSQLException(String query, SQLException se){
        message.append("SQLExeption\n");
        message.append("QUERY: " + query);
        message.append("SQL is in error. SQL was not done.");
    }

    public synchronized void handlelClassNotFoundException(String driver, ClassNotFoundException ne){
        message.append("ClassNotFoundException\n");
        message.append("JDBC DRIVER: " + driver);
        message.append("JDBC Driver can not be found.");
    }

    public String toString(){
        if(throwable != null) {
            message.append(throwable.toString());
        }
        return (message.toString());
    }
}
