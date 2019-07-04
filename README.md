# UART Console 1.0.0

## Getting Started

UART Console is open source PC software written in java for serial communication with target device. 
UARTConsole100.exe built using launch4j can be downloaded [here](http://www.optolab.ftn.uns.ac.rs/index.php/education/project-base/212-uart-console).
View UART Console on [youtube](https://www.youtube.com/watch?v=Pe74BDIj1wE).

### Prerequisites

- Java Development Kit (jdk 1.8.0_102)
- RXTX Java library
- usb4java Java library (usb4java-1.3.0) 
- usb4java-javax extension (usb4java-javax-1.3.0 and jsr80-1.0.1)

### Communication options

- Listing available COM port. Unplugging USB to Serial converter will stop communication and remove port from the list.
- Baud rate selection: 110, 300, 600, 1200, 2400, 4800, 9600, 14400, 19200, 28800, 38400, 56000, 57600, 115200, 128000 and 256000 bps.
- Data bits selection: 8, 7, 6, 5.
- Stop bits selection: 1, 1.5, 2.
- Parity selection: NONE, ODD, EVEN, MARK, SPACE.
- Option of periodically data transmission at: 0.1, 0.2, 0.5, 1, 2, 5 and 10 s.
- Up to 16 different transmission sequences.
- Buttons <LF>, <CR> and <CR+LF> directly send appropriate characters to the communication port.

### Format options

- Transmitter and receiver sequences can be independently displayed in:
	Textual (ASCII -> ISO/IEC 8859-1), 
	Hexadecimal (HEX), 
	Decimal (DEC) and 
	Binary (BIN) format.
- ASCII table is built in UART Console and can be displayed inside of receiver panel 
- In case of HEX, DEC and BIN character “-” represents byte separator
- Write line format automatically appends <CR> and/or <LF> at the end of the sequence to be transmitted
- Read line format reads input buffer only if there is  <CR> and/or <LF> character

### Project options

- New project: creates blank project
- Saving and Loading TRM files (*.trm)
- TRM files (*.trm) contains informations about communication settings (port, baud rate,...) as well as transmitted and received sequences
- Load TRM files is only possible when port is closed

### Themes options

- UART Console 1.0.0 supports two user interface themes: dark and light   
