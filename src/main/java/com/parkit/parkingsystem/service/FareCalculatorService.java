package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;


public class FareCalculatorService {

private boolean isRecurrentUser;


    public void calculateFare(Ticket ticket, boolean isRecurrentUser ){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }
        TicketDAO ticketDAO = new TicketDAO();
        this.isRecurrentUser = ticketDAO.getRecurringUsers(ticket.getVehicleRegNumber());
        long inHour = ticket.getInTime().getTime();
        long outHour = ticket.getOutTime().getTime();

        //TODO: Some tests are failing here. Need to check if this logic is correct --> DONE
        long duration = (outHour - inHour)/60000;
        //
        if (duration > 30 )  {
            Double factor = isRecurrentUser ? (60/0.95) : 60;

            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR / factor);
                    break;
                }
                case BIKE: {
                    ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR / factor);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unkown Parking Type");
            }
        }
        else
        {
            ticket.setPrice(0);
        }

    }
}