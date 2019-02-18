package tfg.shuttlego.model.event;

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
