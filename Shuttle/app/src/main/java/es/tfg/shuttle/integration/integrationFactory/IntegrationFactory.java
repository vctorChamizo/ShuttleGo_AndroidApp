package es.tfg.shuttle.integration.integrationFactory;

public class IntegrationFactory {

    private static IntegrationFactory instance = null;

    public static IntegrationFactory getInstance(){

        if (IntegrationFactory.instance == null)
            IntegrationFactory.instance = new IntegrationFactoryImp();

        return IntegrationFactory.instance;
    }
}
