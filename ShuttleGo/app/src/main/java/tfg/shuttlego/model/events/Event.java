package tfg.shuttlego.model.events;

public enum Event {

    /* ACCOUNT */
    SIGNIN,
    SIGNUP,
    SIGNOUT,

    /* ORIGIN */
    GETORIGINS,
    GETORIGINBYID,
    CREATEORIGIN,
    MODIFYORIGIN,
    DELETEORIGIN,

    /* ROUTE */
    CREATEROUTE,
    SEARCHROUTE,
    ADDTOROUTE
}
