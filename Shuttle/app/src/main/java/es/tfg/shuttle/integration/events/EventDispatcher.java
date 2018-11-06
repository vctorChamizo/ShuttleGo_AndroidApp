package es.tfg.shuttle.integration.events;

import es.tfg.shuttle.integration.requestHandler.RequestHandler;

class EventDispatcher {

    //atributos y metodos del singleton
    private static final EventDispatcher ourInstance = new EventDispatcher();
    static EventDispatcher getInstance() {
        return ourInstance;
    }
    private EventDispatcher() {};

    private RequestHandler rh = new RequestHandler();

    public void dispatchEvent(ShuttleEvents event){

        switch(event){
            case SIGNIN:
            break;
        }

    }
}
