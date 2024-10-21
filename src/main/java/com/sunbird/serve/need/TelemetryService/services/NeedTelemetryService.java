package com.sunbird.serve.need;

import org.springframework.stereotype.Service;
import com.sunbird.serve.need.models.TelemetryRequest.TelemetryDataRequest;
import com.sunbird.serve.need.models.TelemetryRequest.Actor;
import com.sunbird.serve.need.models.TelemetryRequest.Context;
import com.sunbird.serve.need.models.TelemetryRequest.PData;
import com.sunbird.serve.need.models.TelemetryRequest.Events;
import com.sunbird.serve.need.models.Telemetry.TelemetryData;
import com.sunbird.serve.need.models.Telemetry.TelemetryContext;
import com.sunbird.serve.need.models.Telemetry.TelemetryEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

@Service
public class NeedTelemetryService {

    private final TelemetryDataRepository telemetryDataRepository;
    private final TelemetryContextRepository telemetryContextRepository;
    private final TelemetryEventRepository telemetryEventRepository;

    @Autowired
    public NeedTelemetryService(TelemetryDataRepository telemetryDataRepository,
                                TelemetryContextRepository telemetryContextRepository,
                                TelemetryEventRepository telemetryEventRepository) {
        this.telemetryDataRepository = telemetryDataRepository;
        this.telemetryContextRepository = telemetryContextRepository;
        this.telemetryEventRepository = telemetryEventRepository;
    }

    public void saveTelemetryData(TelemetryDataRequest telemetryDataRequest) {
        try {
            // Extract common TelemetryData from request
            String ver = telemetryDataRequest.getVer();
            Long ets = telemetryDataRequest.getEts();

            // Loop through each event in the list and save it
            List<Events> eventsList = telemetryDataRequest.getEvents();
            for (Events event : eventsList) {
                // Extract Event data
                String eid = event.getEid();
                String mid = event.getMid();

                // Extract actor details
                Actor actor = event.getActor();
                String actorId = actor.getId();
                String actorType = actor.getType();

                // Extract context details
                Context context = event.getContext();
                String channel = context.getChannel();
                PData pdata = context.getPdata();

                
                // Save each Cdata associated with the context
               // List<Cdata> cdataList = context.getCdata();
               // for (Cdata cdataReq : cdataList) {
               //     Cdata cdataEntity = new Cdata();
               //     cdataEntity.setType(cdataReq.getType());
               //     cdataEntity.setId(cdataReq.getId());
               //     cdataEntity.setContext(telemetryContext); // Link to context
               //     cdataRepository.save(cdataEntity); // Save Cdata
                //}

                // Create a new TelemetryData entity and populate it
                TelemetryData telemetryData = new TelemetryData();
                telemetryData.setEid(eid);
                telemetryData.setEts(ets);
                telemetryData.setVer(ver);
                telemetryData.setMid(mid);
                telemetryData.setActorId(actorId);
                telemetryData.setActorType(actorType);
                telemetryData.setCreatedAt(Instant.now());
                //telemetryData.setContext(telemetryContext); // Link to context
                TelemetryData savedTelemetryData = telemetryDataRepository.save(telemetryData); // Save TelemetryData

                // Create TelemetryContext
                TelemetryContext telemetryContext = new TelemetryContext();
                telemetryContext.setChannel(channel);
                telemetryContext.setEnv(context.getEnv());
                telemetryContext.setSid(context.getSid());
                telemetryContext.setDid(context.getDid());
                telemetryContext.setPid(pdata.getPid());
                telemetryContext.setPlatform(pdata.getPlatform());
                telemetryContext.setVersion(pdata.getVer());
                telemetryContext.setTelemetryId(savedTelemetryData.getId());
                telemetryContext.setCreatedAt(Instant.now());
                telemetryContextRepository.save(telemetryContext); // Save context


                // Save TelemetryEvent details
                TelemetryEvent telemetryEvent = new TelemetryEvent();
                //telemetryEvent.setTelemetryData(telemetryData);
                telemetryEvent.setType(event.getEdata().getType());
                telemetryEvent.setSubtype(event.getEdata().getSubtype());
                telemetryEvent.setMode(event.getEdata().getMode());
                telemetryEvent.setPageid(event.getEdata().getPageid());
                telemetryEvent.setUri(event.getEdata().getUri());
                telemetryEvent.setVisits(event.getEdata().getVisits());
                telemetryEvent.setTelemetryId(savedTelemetryData.getId());
                telemetryEvent.setCreatedAt(Instant.now());
                telemetryEventRepository.save(telemetryEvent); // Save event
            }
        } catch (Exception e) {
            // Handle any exceptions (logging, rethrowing, etc.)
            System.err.println("Error saving telemetry event: " + e.getMessage());
            throw e;
        }
    }
}
