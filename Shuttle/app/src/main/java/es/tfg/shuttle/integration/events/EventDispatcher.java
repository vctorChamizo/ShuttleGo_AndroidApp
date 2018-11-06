package es.tfg.shuttle.integration.events;

class EventDispatcher {

    //atributos y metodos del singleton
    private static final EventDispatcher ourInstance = new EventDispatcher();
    static EventDispatcher getInstance() {
        return ourInstance;
    }
    private EventDispatcher() {};

    public void dispatchEvent(Events event, Object data){

        switch(event){
            case SIGNIN:

            break;
        }

    }
}
