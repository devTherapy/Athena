package co.retaila.athena.common.enums;

public enum ClientApplicationType {

    SPA, NATIVE, WEB, SERVICE;

    public boolean isPublic() {
        return ClientApplicationType.SPA.equals(this) || ClientApplicationType.NATIVE.equals(this);
    }

    public boolean isConfidential() {
        return ClientApplicationType.WEB.equals(this) || ClientApplicationType.SERVICE.equals(this);
    }

}
