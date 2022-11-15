using System;
using TemplateBasedApplication.Models.Dto;

namespace TemplateBasedApplication.Helper.FileUpload
{
    public class FileUpload
    {

        public static void validateFile(FileUploadDto fileUploadDto)
        {
             validateFileLength(fileUploadDto);
        }

        private static void validateFileLength(FileUploadDto fileUpload)
        {
            if(fileUpload.file.Length < 1) 
            {
                throw new Exception("sad");
            }
        }
    }
}
