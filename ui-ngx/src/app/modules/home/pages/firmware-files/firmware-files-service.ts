import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class FileUploadService {

  httpOptions = {
    headers: new HttpHeaders({ 'auth-token': localStorage.getItem("token") || "" }),
    headers1: new HttpHeaders({ responseType: 'text' })
  };

  constructor(private httpClient:HttpClient) { }
  SERVER_URL: string = "/api/firmware-file";  

  public upload(formData: FormData) {
    console.log("upload service function is called")
    console.log(formData)
    return this.httpClient.post<FormData>(this.SERVER_URL, formData, {  
        reportProgress: true,  
        observe: 'events'  
      });  
  }

  public download(id:any){
    this.httpClient.get(this.SERVER_URL+"/path/"+1,this.httpOptions).subscribe((comp: any) => {
      console.log(comp);
      
    })
  }
}
