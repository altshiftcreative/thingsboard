import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

@Component({
  selector: 'tb-firmware-files',
  templateUrl: './firmware-files.component.html',
  styleUrls: ['./firmware-files.component.scss']
})
export class FirmwareFilesComponent implements OnInit {
  fileName = '';
  file: any | undefined;
  pathsArray: string[] = [];

  linksArray: any[]=[];

  constructor(private http: HttpClient, private sanitization: DomSanitizer) { }
  ngOnInit(): void {
   this.getLinks();
  }

  onFileSelected(event: any) {

    const file: File = event.target.files[0];
    const fileNew: File = event.target.value;
    console.log("FILES : ", fileNew);
    console.log("FILES22 : ", file);


    let fileReader = new FileReader();
    fileReader.onload = (e) => {
      console.log(fileReader.result);

      

      if (file) {
        this.file = file;
        this.fileName = file.name;



        const fileBody = {
          "file": fileReader.result,
          "modelNumber": "22",
          "fileType": file.type,
          "deviceType": "one",
          "firmwareVersion": "0.2",
          "checksum": "22",
          "fileName":file.name
        }

        this.http.post("/api/firmware-file", fileBody).subscribe(data => {
          console.log("DATAAA POST: ", data);
          this.getLinks();
        });

      }

    }
    fileReader.readAsDataURL(file);
  }


  getLinks(){
    this.http.get("/api/firmware-file", { withCredentials: true }).subscribe(data => {
      console.log("DATAAA GET: ", data);
      this.fillArray(data);
    });
  }

  fillArray(data: any) {
    this.pathsArray = [];
    data.forEach((e: any) => {
      if (e.file !== null) {
        this.pathsArray.push(e);
      }
    });
  }

}