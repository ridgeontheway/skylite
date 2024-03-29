package com.example.skylite.Services;

import android.content.Context;

/**
 * Kelsey Osos
 * This class is the singleton factory for all services.
 * It ensures we only instantiate each service once within the context.
 * It provides a one-stop interface to access any service from anywhere.
 **/
public class ServiceBase {
    private IConstellationService _constellationService;
    private IWikiService _wikiService;
    private IJsonService _json;
    private IEventsService _events;
    private static ServiceBase _services;

    public ServiceBase(Context context) {
        _constellationService = new ConstellationService(context);
        _events = new EventsService(context);
    }

    // We want to access all services statically, so init is called with a new ServiceBase constructor
    public static void init(ServiceBase services) {
        _services = services;
        _services.setWikiService();
        _services.setJsonService();
        _services._constellationService.populateList();
        _services._events.populateList();
    }

    public static IConstellationService constellationService() {
        return _services._constellationService;
    }

    private void setWikiService() {
        this._wikiService = new WikiService();
    }

    public static IWikiService wikiService() {
        return _services._wikiService;
    }

    private void setJsonService() {
        this._json = new JsonService();
    }

    static IJsonService jsonService() {
        return _services._json;
    }

    public static IEventsService eventsService() {
        return _services._events;
    }
}
