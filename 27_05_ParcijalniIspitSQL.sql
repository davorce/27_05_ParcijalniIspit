--Izrada baze
CREATE DATABASE JavaAdv
GO
USE JavaAdv
GO

--Izrada tablica
CREATE TABLE Polaznik(
	PolaznikID int CONSTRAINT PK_Polaznik PRIMARY KEY IDENTITY,
	Ime nvarchar(100) NOT NULL,
	Prezime nvarchar(100) NOT NULL
);

--############--
CREATE TABLE ProgramObrazovanja(
	ProgramObrazovanjaID int CONSTRAINT PK_ProgramObrazovanja PRIMARY KEY IDENTITY,
	Naziv nvarchar(100) NOT NULL,
	CSVET int NOT NULL
);

--############--
CREATE TABLE Upis(
	UpisID int CONSTRAINT PK_Upis PRIMARY KEY IDENTITY,
	IDProgramObrazovanja int NOT NULL,
	IDPolaznik int NOT NULL,
	CONSTRAINT FK_Upis_ProgramObrazovanja FOREIGN KEY (IDProgramObrazovanja) REFERENCES ProgramObrazovanja(ProgramObrazovanjaID),
	CONSTRAINT FK_Upis_Polaznik FOREIGN KEY (IDPolaznik) REFERENCES Polaznik(PolaznikID)
);

--Izrada procedura
CREATE PROCEDURE NoviPolaznik
	@Ime nvarchar(100),
	@Prezime nvarchar(100)
AS
BEGIN
	INSERT INTO Polaznik (Ime, Prezime)
	VALUES (@Ime, @Prezime);
END

--############--
CREATE PROCEDURE NoviProgram
	@Naziv nvarchar(100),
	@CSVET nvarchar(100)
AS
BEGIN
	INSERT INTO ProgramObrazovanja (Naziv, CSVET)
	VALUES (@Naziv, @CSVET);
END

--############--
CREATE PROCEDURE UpisiPolaznika
	@IDPolaznik int,
	@IDProgramObrazovanja int
AS
BEGIN
	INSERT INTO Upis (IDPolaznik, IDProgramObrazovanja)
	VALUES (@IDPolaznik, @IDProgramObrazovanja);
END

--############--
CREATE PROCEDURE PrebaciPolaznika
	@IDPolaznik int,
	@CurrProgramID int,
	@NewProgramID int
AS
BEGIN
	BEGIN TRANSACTION;
	DELETE FROM Upis WHERE IDPolaznik = @IDPolaznik AND IDProgramObrazovanja = @CurrProgramID;
	INSERT INTO Upis (IDPolaznik, IDProgramObrazovanja) VALUES (@IDPolaznik,@NewProgramID);
	COMMIT TRANSACTION;
END

--############--
CREATE PROCEDURE IspisPolaznikaPoProgramu
	@ProgramID int
AS
BEGIN
	SELECT p.Ime, p.Prezime, po.Naziv, po.CSVET
	FROM Polaznik p
	INNER JOIN UPIS u ON p.PolaznikID = u.IDPolaznik
	INNER JOIN ProgramObrazovanja po ON u.IDProgramObrazovanja = po.ProgramobrazovanjaID
	WHERE po.ProgramObrazovanjaID = @ProgramID;
END