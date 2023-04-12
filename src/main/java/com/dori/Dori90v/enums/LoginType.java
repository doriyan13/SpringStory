package com.dori.Dori90v.enums;

public enum LoginType {
    /*	* 3: ID deleted or blocked
     * 4: Incorrect password
     * 5: Not a registered id
     * 6: System error
     * 7: Already logged in
     * 8: System error
     * 9: System error
     * 10: Cannot process so many connections
     * 11: Only users older than 20 can use this channel
     * 13: Unable to log on as master at this ip
     * 14: Wrong gateway or personal info and weird korean button
     * 15: Processing request with that korean button!
     * 16: Please verify your account through email...
     * 17: Wrong gateway or personal info
     * 21: Please verify your account through email...
     * 23: License agreement
     * 25: Maple Europe notice
     * 27: Some weird full client notice, probably for trial versions
     * 32: IP blocked
     * 84: please revisit website for pass change --> 0x07 recv with response 00/01*/
    Success(0),
    TempBlocked(1),
    Blocked(2),
    Abandoned(3),
    IncorrectPassword(4),
    NotRegistered(5),
    DBFail(6),
    AlreadyConnected(7),
    NotConnectableWorld(8),
    Unknown(9),
    Timeout(10),
    NotAdult(11),
    AuthFail(12),
    ImpossibleIP(13),
    NotAuthorizedNexonID(14),
    NoNexonID(15),
    IncorrectSSN2(16),
    WebAuthNeeded(17),
    DeleteCharacterFailedOnGuildMaster(18),
    TempBlockedIP(19),
    IncorrectSPW(20),
    DeleteCharacterFailedEngaged(21),
    SamePasswordAndSPW(22),
    WaitOTP(23),
    WrongOTP(24),
    OverCountErrOTP(25),
    SystemErr(26),
    CancelInputDeleteCharacterOTP(27),
    PaymentWarning(28),
    DeleteCharacterFailedOnFamily(29),
    InvalidCharacterName(30),
    IncorrectSSN(31),
    SSNConfirmFailed(32),
    SSNNotConfirmed(33),
    WorldTooBusy(34),
    OTPReissuing(35),
    OTPInfoNotExist(36),
    Shutdowned(37),
    DeleteCharacterFailedHasEntrustedShop(38),
    AlbaPerform(39),
    TransferredToNxEmailID(40),
    UntransferredToNxEmailID(41),
    RequestedMapleIDAlreadyInUse(42),
    WaitSelectAccount(43),
    DeleteCharacterFailedProtectedItem(44),
    UnauthorizedUser(45),
    CannotCreateMoreMapleAccount(46),
    CreateBanned(47),
    CreateTemporarilyBanned(48),
    EventNewCharacterExpireFail(49),
    SelectiveShutdowned(50),
    NonownerRequest(51),
    OTPRequired(52),
    GuestServiceClosed(53),
    BlockedNexonID(54),
    DupMachineID(55),
    NotActiveAccount(56),
    IncorrectSPW4th(57),
    IncorrectSPW5th(58),
    InsufficientSPW(59),
    SameCharSPW(60),
    WebLaunchingOTPRequired(61),
    MergeWorld_CreateCharacterBanned(62),
    ChangeNewOTP(63),
    BlockedByServiceArea(64),
    ExceedReservedDeleteCharacter(65),
    UnionFieldChannelClosed(67),
    ProtectAccount(68),
    AntiMacroReq(69),
    AntiMacroCreateFailed(70),
    AntiMacroIncorrect(71),
    LimitCreateCharacter(72),
    ProtectSSOLogin(73),
    InvalidMapleIDThroughMobile(74),
    InvalidPasswordThroughMobile(75),
    HashedPasswordIsEmpty(76),
    NGS_For_Ass(77),
    AlreadyConnectedThroughMobile(78),
    Protected_For_Ass(79),
    Blocked_For_Ass(80),
    WrongVer(81),
    EMailVerify(82),
    DenyJob(83),
    InvalidObject(84),
    IncorrectLoginType_OtherToMapleID(85),
    FailedUserCreate(86),
    MobileTokenInvalid(87),
    MobileTokenDeviceIDInvalid(88),
    MobileTokenExpired(89),
    NotHaveNaverID(90),
    UserTossAIPlayer(91),
    InactivateMember(92),
    ProcFail(-1),
    ;
    private final byte value;

    LoginType(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }
}
