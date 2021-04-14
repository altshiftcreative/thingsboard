import { HttpClient } from '@angular/common/http';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { FileUploadService } from './firmware-files-service';

@Component({
  selector: 'tb-firmware-files',
  templateUrl: './firmware-files.component.html',
  styleUrls: ['./firmware-files.component.scss']
})
export class FirmwareFilesComponent implements OnInit {
  @ViewChild("fileUpload", { static: false })
  fileUpload!: ElementRef;
   files:any  = [];  
  fileName:string | undefined;

  constructor(private fileUploadService:FileUploadService) { }


  ngOnInit() {
  }


  onClick() { 
    try {
      const fileUpload = this.fileUpload.nativeElement;fileUpload.onchange = () => {  
        for (let index = 0; index < fileUpload.files.length; index++)  
        {  
         const file = fileUpload.files[index];  
          this.fileName = file.name +" is uploaded"
         
         this.files.push({ data: file, inProgress: false, progress: 0});  
        }  
          this.uploadFiles();  
        };  
        fileUpload.click();  
    } catch (error) {
      
    } 
    
}
private uploadFiles() {  
  this.fileUpload.nativeElement.value = '';  
  this.files.forEach((file: any) => {  
    this.uploadFile(file);  
  });  
}
uploadFile(file: any) {  
  const formData = new FormData();  
  formData.append('file', file.data);  
  file.inProgress = true;  
  this.fileUploadService.upload(formData).subscribe(
    rsp => {
      console.log(rsp.type)


     
},
    error => {
      console.log(error)
    });

}

download(){  
  this.fileUploadService.download(7);
}

}