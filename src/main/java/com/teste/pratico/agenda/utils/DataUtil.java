package com.teste.pratico.agenda.utils;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DataUtil {

    public static Timestamp stringParaTimestamp(String data) {

        LocalDate localDate = LocalDate.parse(data, DateTimeFormatter.ISO_LOCAL_DATE);
        return new Timestamp(Date.valueOf(localDate).getTime());
    }

    public static LocalDate stringParaLocalDate(String data) {

        Timestamp dataInformada = DataUtil.stringParaTimestamp(data);
        return dataInformada.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDate timestampParaLocalDate(Timestamp data) {
        
        return data.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Integer calcularDiasEntre(LocalDate inicio, LocalDate fim) {

        return (int) ChronoUnit.DAYS.between(inicio, fim);
    }
}