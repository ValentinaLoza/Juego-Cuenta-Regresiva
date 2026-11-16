package logica;

public enum Rol {
    PROTAGONISTA,
    MUJER,
    NINO,
    NINA;

    public boolean esNino() {
        return this == NINO || this == NINA;
    }
}
