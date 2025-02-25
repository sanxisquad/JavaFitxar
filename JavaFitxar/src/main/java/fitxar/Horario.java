package fitxar;

public class Horario {
    private int professorId;
    private String diaSemana;
    private String dia;
    private String horaInicio;
    private String horaFin;

    public Horario(int professorId, String diaSemana, String dia, String horaInicio, String horaFin) {
        this.professorId = professorId;
        this.diaSemana = diaSemana;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public Horario(String dia, String horaInicio, String horaFin) {
        this(0, dia, dia, horaInicio, horaFin); // Default values for professorId and diaSemana
    }

    // Getters y Setters
    public int getProfessorId() {
        return professorId;
    }

    public void setProfessorId(int professorId) {
        this.professorId = professorId;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }
}
