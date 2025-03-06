package com.example.Calmora.google;

import com.example.Calmora.appointment.Appuntamento;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.TimeZone;

@Service
public class CalendarService {

    @Autowired
    private final CalendarConfig calendarConfig;

    @Autowired
    public CalendarService(CalendarConfig calendarConfig) {
        this.calendarConfig = calendarConfig;
    }

    public String createMeetLink() {
        try {
            Calendar service = calendarConfig.getCalendarService();

            Event event = new Event()
                    .setSummary("Stanza virtuale dello Psicologo")
                    .setDescription("Questa è la stanza permanente per gli appuntamenti.");


            ZonedDateTime futureDate = ZonedDateTime.now().plusYears(10);
            DateTime startDateTime = new DateTime(futureDate.toInstant().toString());
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone(TimeZone.getDefault().getID());
            event.setStart(start);
            event.setEnd(start); // la stanza non avrà scadenza

            event = service.events().insert("primary", event).execute();
            return event.getHangoutLink(); // ritorna il link Meet
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String createEvent(Appuntamento appuntamento) {
        try {
            Calendar service = CalendarConfig.getCalendarService();

            // Recupera il link Meet dallo psicologo associato all'appuntamento
            String meetLink = appuntamento.getPsicologo().getUrlMeet();

            Event event = new Event()
                    .setSummary("Appuntamento con " + appuntamento.getPsicologo().getName())
                    .setDescription("Colloquio tra il paziente e lo psicologo.")
                    .setLocation(meetLink) // Usa il link dello psicologo
                    .setAttendees(List.of(
                            new EventAttendee().setEmail(appuntamento.getPsicologo().getEmail()),
                            new EventAttendee().setEmail(appuntamento.getPaziente().getEmail())
                    ));

            // Converti LocalDateTime in DateTime per Google Calendar
            DateTime startDateTime = new DateTime(
                    appuntamento.getDataAppuntamento()
                            .atZone(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli()
            );

            DateTime endDateTime = new DateTime(
                    appuntamento.getDataFineAppuntamento()
                            .atZone(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli()
            );

            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone(TimeZone.getDefault().getID());

            EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone(TimeZone.getDefault().getID());

            event.setStart(start);
            event.setEnd(end);

            event = service.events().insert("primary", event).execute();

            return meetLink; // Restituisci il link Meet dello psicologo
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String addEventToCalendar(Appuntamento appuntamento){
        return createEvent(appuntamento);
    }
}
