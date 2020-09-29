package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;


public class FareCalculatorService {
//Add boolean isRecurrentUser as parameter to know if user is recurring or not
public void calculateFare(Ticket ticket, boolean isRecurrentUser ){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        //Returns if the user is recurring or not
        TicketDAO ticketDAO = new TicketDAO();
        ticketDAO.getRecurringUsers(ticket.getVehicleRegNumber());
        //Returns the number of milliseconds associated to field InTime
        long inHour = ticket.getInTime().getTime();
        //Returns the number of milliseconds associated to field OutTime
        long outHour = ticket.getOutTime().getTime();

        //TODO#1: Some tests are failing here. Need to check if this logic is correct --> DONE
        //Operation to calculate the duration in minutes
        long duration = (outHour - inHour)/60000;
        //If duration is superior to 30 minutes the user will pay a fee
        if (duration > 30 )  {
            //If the user is recurring a discount of 5% is applied
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
        //If duration is less than 30 minutes the user will park for free
        else
        {
            ticket.setPrice(0);
        }

    }
}