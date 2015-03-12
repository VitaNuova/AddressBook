package cz.fi.muni.pv168.AddressBook;

/**
 * Created by Виктория on 10-Mar-15.
 */
public class ServiceFailureException extends RuntimeException {

        public ServiceFailureException(String msg) {
            super(msg);
        }

        public ServiceFailureException(Throwable cause) {
            super(cause);
        }

        public ServiceFailureException(String message, Throwable cause) {
            super(message, cause);
        }

}
