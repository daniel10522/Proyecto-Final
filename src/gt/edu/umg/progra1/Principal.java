
package gt.edu.umg.progra1;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import javax.swing.JOptionPane;

/*
 * @author daniel10522
 */
public class Principal {
    Scanner sc = new Scanner (System.in);
    RandomAccessFile fichero = null, entidades = null, atributos = null;
    private final String rutaBase = "C:\\Users\\daniel10522\\Desktop\\Proyecto_Final\\Proyecto2";
    private final String rutaEntidades = "C:\\Users\\daniel10522\\Desktop\\Proyecto_Final\\Proyecto2\\entidades.dat";
    private final String rutaAtributos = "C:\\Users\\daniel10522\\Desktop\\Proyecto_Final\\Proyecto2\\atributos.dat";  
    private final int totalBytes = 83, bytesEntidades = 47, bytesAtributo = 43;
    private final static String formatoFecha = "dd/mm/yy";
    static DateFormat format = new SimpleDateFormat(formatoFecha);
 
     private List<Entidad> listaEntidades = new ArrayList<>();
    
    public static void main(String[] args) {
        Principal p = new Principal();
        if(p.validarDefinicion()){
            p.menuDefinicion(true);
        }else{
            p.menuDefinicion(false);
        }
        System.exit(0);
    }
    
    
    private boolean validarDefinicion(){
        boolean respuesta = false;
        try{
            entidades = new RandomAccessFile(rutaEntidades, "rw");
            atributos = new RandomAccessFile(rutaAtributos, "rw");
            long longitud = entidades.length();
            if(longitud <= 0){
                JOptionPane.showMessageDialog(null, "NO EXISTEN REGISTROS", "IMPORTANTE", JOptionPane.INFORMATION_MESSAGE);
                respuesta = false;
            }
            if(longitud >= bytesEntidades){
                entidades.seek(0);
                Entidad ent;
                while(longitud >= bytesEntidades){
                    ent = new Entidad();
                    ent.setIndice(entidades.readInt());
                    byte[] bnombre = new byte[30];
                    entidades.read(bnombre);
                    ent.setBytesNombre(bnombre);
                    ent.setCantidad(entidades.readInt());
                    ent.setBytes(entidades.readInt());
                    ent.setPosicion(entidades.readLong());
                    entidades.readByte();
                    longitud -=bytesEntidades;
                    
                    long longitudAtributos = atributos.length();
                    if(longitudAtributos <= 0){
                        JOptionPane.showMessageDialog(null, "NO EXISTEN REGISTROS", "IMPORTANTE", JOptionPane.INFORMATION_MESSAGE);
                        respuesta = false;
                        break;
                    }
                    atributos.seek(ent.getPosicion());
                    Atributos a;
                    longitudAtributos = ent.getCantidad() * bytesAtributo;
                    while(longitudAtributos >= bytesAtributo){
                        a = new Atributos();
                        a.setIndice(atributos.readInt());
                        byte[] bNombreAtributo = new byte[30];
                        atributos.read(bNombreAtributo);
                        a.setBytesNombre(bNombreAtributo);
                        a.setValorTipoDato(atributos.readInt());
                        a.setLongitud(atributos.readInt());
                        a.setNombreTipoDato();
                        atributos.readByte();
                        ent.setAtributo(a);
                        longitudAtributos -= bytesAtributo;
                    }
                    listaEntidades.add(ent);  
                }
                if(listaEntidades.size() > 0){
                    respuesta = true;
                }
            }
                    
        }catch(Exception e){
            e.printStackTrace();
        }
        return respuesta;
    }
    
    private void mostrarEntidades(Entidad entidad){
        System.out.println("Indice: "+ entidad.getIndice());
        System.out.println("Nombre: "+ entidad.getNombre());
        System.out.println("Cantidad de Atributos: "+ entidad.getCantidad());
        System.out.println("Atributos: ");
        int i =1;
        for (Atributos atributo : entidad.getAtributos()) {
            System.out.println("\tNo. "+i);
            System.out.println("\tNombre: "+atributo.getNombre());
            System.out.println("\tTipo de Dato: "+ atributo.getNombreTipoDato());
            if(atributo.isRequiereLongitud()){
                System.out.println("\tLongitud: "+atributo.getLongitud());
            }
            i++;
        }
    }
    
    private boolean agregarEntidad(){
        boolean resultado = false;
        try{
            Entidad entidad = new Entidad();
            entidad.setIndice(listaEntidades.size() + 1);
            String strNombre = "";
            int longitud = 0;
            do{ 
                 strNombre = JOptionPane.showInputDialog(null, "Ingrese el Nombre de la Entidad");
                longitud = strNombre.length();
                if(longitud < 2 || longitud > 30){
                    JOptionPane.showMessageDialog(null, "La longitud del Nombre no es Valida (3-30)", "IMPORTANTE", JOptionPane.INFORMATION_MESSAGE);                
                }else{
                    if(strNombre.contains(" ")){
                       JOptionPane.showMessageDialog(null, "El Nombre no Puede Tener Espacios, Sustituya con guion bajo", "IMPORTANTE", JOptionPane.INFORMATION_MESSAGE);
                        longitud = 0;
                    }
                }  
            }while(longitud < 2 || longitud>30);
            entidad.setNombre(strNombre);
            //System.out.println("Atributos de la Entidad");
            int bndDetener = 0;
            do{
                Atributos atributos = new Atributos();
                atributos.setIndice(entidad.getIndice());
                longitud = 0;
                do{
                    strNombre = JOptionPane.showInputDialog(null, "Escriba el Nombre del Atributo no. "+ (entidad.getCantidad()+1));
                    longitud = strNombre.length();
                    if(longitud<2 || longitud>30){
                         JOptionPane.showMessageDialog(null, "La Longitud del Nombre no es Valida (3 - 30)", "ADVERTENCIA", JOptionPane.WARNING_MESSAGE);     
                    }else{
                        if(strNombre.contains(" ")){
                            JOptionPane.showMessageDialog(null, "El Nombre no debe contener espacios, utilize guion bajo ","ADVERTENCIA", JOptionPane.WARNING_MESSAGE);
                             longitud = 0;
                        }
                    }  
                }while(longitud < 2 || longitud > 30);
                atributos.setNombre(strNombre);
                System.out.println("Seleccione Tipo de Dato");
                System.out.println(TipoDato.INT.getValue() + " .......... " + TipoDato.INT.name());
                System.out.println(TipoDato.LONG.getValue() + " .......... " + TipoDato.LONG.name());
                System.out.println(TipoDato.STRING.getValue() + " .......... " + TipoDato.STRING.name());
                System.out.println(TipoDato.DOUBLE.getValue() + " .......... " + TipoDato.DOUBLE.name());
                System.out.println(TipoDato.FLOAT.getValue() + " .......... " + TipoDato.FLOAT.name());
                System.out.println(TipoDato.DATE.getValue() + " .......... " + TipoDato.DATE.name());
                System.out.println(TipoDato.CHAR.getValue() + " .......... " + TipoDato.CHAR.name());
                atributos.setValorTipoDato(sc.nextInt());
                if(atributos.isRequiereLongitud()){
                    System.out.println("Ingrese la Longitud");
                    atributos.setLongitud(sc.nextInt());
                }else{
                    atributos.setLongitud(0);
                }
                atributos.setNombreTipoDato();
                entidad.setAtributo(atributos);
                bndDetener = Integer.parseInt(JOptionPane.showInputDialog(null,"¿Desea agregar otro atributo?\n Si presione cualquier numero\n, No presione 0"));
            }while(bndDetener != 0);
            mostrarEntidades(entidad);
            //System.out.println("Presione 1 para guardar 0 para cancelar");
            longitud = Integer.parseInt(JOptionPane.showInputDialog(null, "Esta Seguro de Registrar los Datos?\n"
                    + "1->>Guardar\n0-->>Cancelar"));
            if(longitud == 1){
                entidad.setPosicion(atributos.length());
                atributos.seek(atributos.length());
                for(Atributos atributo : entidad.getAtributos()){
                    atributos.writeInt(atributo.getIndice());
                     atributos.write(atributo.getBytesNombre());
                    atributos.writeInt(atributo.getValorTipoDato());
                     atributos.writeInt(atributo.getLongitud());
                    atributos.write("\n".getBytes());
                }
                entidades.writeInt(entidad.getIndice());
                entidades.write(entidad.getBytesNombre());
                entidades.writeInt(entidad.getCantidad());
                entidades.writeInt(entidad.getBytes());
                entidades.writeLong(entidad.getPosicion());
                entidades.write("\n".getBytes());
                listaEntidades.add(entidad);
                resultado = true;    
            }else{
                JOptionPane.showMessageDialog(null ,"No se ha Guardado la Entidad porque el Usuario decidio Cancelar", "ERROR", JOptionPane.ERROR_MESSAGE);
                resultado = false;
            }    
            System.out.println("Presione una tecla para continuar...");
            System.in.read();
        }catch(Exception e){
            resultado = false;
            e.printStackTrace();
        }
        return resultado;
    }   
    
    private void modificarEntidad(){
    try{
        int indice = 0;
        while(indice<1 || indice>listaEntidades.size()){
            for(Entidad entidad : listaEntidades){
               System.out.println(entidad.getIndice() + "..... "+ entidad.getNombre());
            }
           System.out.println("Seleccione la Entidad que Desea Modificar");
           indice = sc.nextInt();
       }
       Entidad entidad = null;
       for(Entidad e : listaEntidades){
           if(indice == e.getIndice()){
               entidad = e;
               break;
           }
       }
       String nombreFichero = formarNombreFichero(entidad.getNombre());
       fichero = new RandomAccessFile(rutaBase + nombreFichero, "rw");
       long longitudDatos = fichero.length();
       fichero.close();
       if(longitudDatos > 0){
           JOptionPane.showMessageDialog(null, "La Entidad no se Puede Modificar porque ya Tiene Datos");      
       }else{
           boolean bdnEncontrado = false, bdnModificado = false;
           entidades.seek(0);
           long longitud = entidades.length();
           int registros = 0, salir = 0, i;
           Entidad e;
           byte[] tmpBytes;
           while(longitud > totalBytes){
               e = new Entidad();
               e.setIndice(entidades.readInt());
               tmpBytes = new byte[30];
               entidades.read(tmpBytes);
               e.setBytesNombre(tmpBytes);
               e.setCantidad(entidades.readInt());
               e.setBytes(entidades.readInt());
               e.setPosicion(entidades.readLong());
               if(entidad.getIndice() == e.getIndice()){
                   System.out.println("Si no desea Modificar el Campo Prsione Enter");
                   System.out.println("Ingrese el Nombre");
                   String tmpStr = "";
                   int len = 0;
                   long posicion;
                    do{
                        tmpStr = sc.nextLine();
                         len = tmpStr.length();
                        if(len== 1 || len>30){
                           JOptionPane.showMessageDialog(null, "La Longitud no es Valida (2 - 30)");
                        }      
                    }while(len==1 || len>30);
                    if(len > 0){
                        e.setNombre(tmpStr);
                        posicion = registros * totalBytes;
                        fichero.seek(posicion);
                        fichero.skipBytes(4);
                        fichero.write(e.getBytesNombre());
                        bdnModificado = true;
                   }
                   i = 1;
                   for(Atributos a : entidad.getAtributos()){
                       System.out.println("Modificando Atributo 1");
                       System.out.println(a.getNombre().trim());
                    }
                   break;
                }
                registros++;
                longitud -= totalBytes;            
            }
        }
           
   }catch(Exception e){
       System.out.println("Error: "+ e.getMessage());
    }    
}   
   
private void menuDefinicion(boolean mostrarAgregarRegistros){
    int opcion = 0;
    while(opcion !=6){ 
        try{
            opcion = Integer.parseInt(JOptionPane.showInputDialog(null,
                    "Elija una opcion\n" 
                    +"1---Agregar Entidad\n"
                    +"2---Modificar Entidad\n"
                    + "3---listar Entidad\n"
                    +"4---Agregar Registros\n"
                    +"5---Eliminar Bases de Datos\n"
                    + "6---Salir", "Menu de Opciones",3));
        switch(opcion){
            case 1:
                //Agregar Entidad
                if(agregarEntidad()){
                    JOptionPane.showMessageDialog(null, "Entidad Agregada Con Exito", "IMPORTANTE", JOptionPane.INFORMATION_MESSAGE);
                    mostrarAgregarRegistros = true;
                }
                break;

            case 2:
                //Modificar Entidad
                modificarEntidad();
                break;

            case 3:
                //Listar Entidad
                   if(listaEntidades.size()>0){
                       int tmpInt = 0;
                       System.out.println("Desea imprimir los detalles. Si, presione 1. No, presione 0?");
                       tmpInt = Integer.parseInt(JOptionPane.showInputDialog(null, "¿Desea imprimir los detalles?\n Si, presione 1.\nNo, presione 0?" ));
                       if(tmpInt == 1){
                           for(Entidad entidad : listaEntidades){
                               mostrarEntidades(entidad);
                           }
                       }else{
                           for(Entidad entidad : listaEntidades){
                               System.out.println("indice: " + entidad.getIndice());
                               System.out.println("Nombre: " + entidad.getNombre());
                               System.out.println("Cantidad de Atributos: " + entidad.getCantidad());
                           }
                       }
                   }else{
                       System.out.println("No hay Entidades Registradas");
                   }
                   break;

            case 4:
                //Agregar Registro
                int indice = 0;
                while(indice<1 || indice>listaEntidades.size()){
                    for(Entidad entidad : listaEntidades){
                        System.out.println(entidad.getIndice() + "---" + entidad.getNombre());
                    }
                    System.out.println("Seleccione la Entidad");
                    indice = sc.nextInt();
                }
                iniciar(indice);
                break;
            
            case 5:
                //ELiminar Base de Datos
                int confirmar = 0;
                confirmar = Integer.parseInt(JOptionPane.showInputDialog(null, "Esta seguro de borrar los archivos de base de datos, presione 1 de lo contrario cualquier numero para cancelar?\n Esta accion no se podra reversar", "ADVERTENCIA",JOptionPane.WARNING_MESSAGE ));
                if(confirmar == 1){
                    cerrarArchivos();
                        if(borrarArchivos()){
                            listaEntidades = null;
                            listaEntidades = new ArrayList<>();
                            mostrarAgregarRegistros = false;
                            JOptionPane.showMessageDialog(null,"Base de Datos Eliminada con Exito", "ELIMINAR", JOptionPane.INFORMATION_MESSAGE);
                        }
                }
                break;
                
            case 6:
                JOptionPane.showMessageDialog(null, "Gracias por Utilizar Nuestro Sistema");
                break;

            default:
                JOptionPane.showMessageDialog(null, "Opcion Invalida", "ERROR", JOptionPane.ERROR_MESSAGE);
                break;
        }
      }catch(Exception e){
      JOptionPane.showMessageDialog(null, "Debe Seleccionar Una Opcion", "ERROR", JOptionPane.ERROR_MESSAGE);
    }
        }       
}

    private void cerrarArchivos(){
        if(fichero != null){
            try{
                fichero.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        
        if(atributos != null){
            try{
                atributos.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        
        if(entidades != null){
            try{
                entidades.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public boolean borrarArchivos(){
        boolean res = false;
        try{
            File file;
            for(Entidad entidad : listaEntidades){
                file = new File(rutaBase + entidad.getNombre().trim()+ ".dat");
                if(file.exists()){
                    file.delete();
                }
                file = null;
            }
            file = new File(rutaAtributos);
            if(file.exists()){
                file.delete();
            }
            file = null;
            file = new File(rutaEntidades);
            if(file.exists()){
                file.delete();
            }
            file = null;
            res = true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return res;    
    }
    
   private String formarNombreFichero(String nombre) {
	return nombre.trim() + ".dat";
    }
    
   //METODO PARA GRABAR REGISTROS
    private void iniciar(int indice){
        int opcion = 0;
         String nombreFichero = "";
        try{
            Entidad entidad = null;
            for(Entidad e : listaEntidades){
                if(indice == e.getIndice()){
                    nombreFichero = formarNombreFichero(e.getNombre());
                    entidad = e;
                    break;
                }
            }
           fichero = new RandomAccessFile(rutaBase + nombreFichero, "rw");
           System.out.println("Bienvenidos");
           Atributos a = entidad.getAtributos().get(0);
           do{
               try{
                    System.out.println("Seleccione una Opcion");
                    System.out.println("1---Agregar");
                    System.out.println("2---Listar");
                    System.out.println("3---Modificar"); 
                    System.out.println("4---Eliminar");
                    System.out.println("0---Regresar al Menu Anterior");
                    opcion = sc.nextInt();
                    switch(opcion){
                        case 0:
                            System.out.println("");
                            break;
                        
                        case 1:
                            grabarRegistro(entidad);
                            break;
                          
                        case 2:
                            listarRegistros(entidad);
                             break;
                        
                        case 3:
                            System.out.println("Ingrese el carne a modificar: ");
                            //carne = sc.nextInt();
                            //sc.nextLine();
                            modificarRegistros();
                            break;
                        
                        default:
                            System.out.println("Opcion Invalida");
                            break;
                    }                  
                }catch(Exception e){
                    System.out.println("Error: " + e.getMessage());
                }           
            }while(opcion != 0);           
        }catch(Exception e){
            System.out.println("Error: "+ e.getMessage());
        }
    }
    
    private boolean grabarRegistro(Entidad entidad){
        boolean resultado = false;
        try{
            fichero.seek(fichero.length());
            boolean valido;
            byte[] bytesString;
            String tmpString = "";
            for(Atributos atributo : entidad.getAtributos()){
                valido = false;
                System.out.println("Ingrese "+ atributo.getNombre().trim());
                while(!valido){
                    try{
                        switch(atributo.getTipoDato()){
                            case INT:
                                int tmpInt = sc.nextInt();
                                fichero.writeInt(tmpInt);
                                sc.nextLine();
                                break;
                            
                            case LONG:
                                long tmpLong = sc.nextLong();
                                fichero.writeLong(tmpLong);
                                break;
                            
                            case STRING:
                                int longitud = 0;
                                do{
                                   tmpString = sc.nextLine();
                                   longitud = tmpString.length();
                                   if(longitud <=1 || longitud>atributo.getLongitud()){
                                       System.out.println("La longitud de "+ atributo.getNombre().trim() 
                                                + " no es valida [1- "+ atributo.getLongitud() + "]");   
                                    }    
                                }while(longitud <= 0 || longitud > atributo.getLongitud());
                                bytesString = new byte[atributo.getLongitud()];
                                for(int i=0; i<tmpString.length(); i++){
                                    bytesString[i] = (byte) tmpString.charAt(i);
                                }
                                fichero.write(bytesString);
                                break;
                            
                            case DOUBLE:
                                double tmpDouble = sc.nextDouble();
                                fichero.writeDouble(tmpDouble);
                                break;
                                
                            case FLOAT:
                                float tmpFloat = sc.nextFloat();
                                fichero.writeFloat(tmpFloat);
                                break;
                            
                            case DATE:
                                Date date = null;
                                tmpString = "";
                                while(date == null){
                                    System.out.println("Formato de Fecha: " + formatoFecha);
                                    tmpString = sc.nextLine();
                                    date = stringToDate(tmpString);
                                }
                                bytesString = new byte[atributo.getBytes()];
                                for(int i=0; i<tmpString.length(); i++){
                                    bytesString[i] = (byte) tmpString.charAt(i);
                                }
                                fichero.write(bytesString);
                                break;
                                
                            case CHAR:
                                do{
                                    tmpString = sc.nextLine();
                                    longitud = tmpString.length();
                                    if(longitud <1 || longitud>1){
                                        System.out.println("Solo se Permite un Caracter");
                                    }
                                }while(longitud<1 || longitud>1);
                                byte caracter = (byte) tmpString.charAt(0);
                                fichero.writeByte(caracter);
                                break;
                        }
                        valido = true;
                    }catch(Exception e){
                        System.out.println("Error "+ e.getMessage()+ " al capturar tipo de dato, ingrese de nuevo el valoro: ");
                        sc.nextLine();
                    }
                }
            }
            fichero.write("\n".getBytes());
            resultado = true;
        }catch(Exception e){
            resultado = false;
            System.out.println("Error al agregar el registro " + e.getMessage());
        }
        return resultado;
    }
    
    //Metodo para Modificar Registros
    public void modificarRegistros(){
        
    }
    
    
    //Metodo para Eliminar Registros
        public void eliminarRegistros(){
            
            
            
        }
        
    //Metodo para Listar Registros
    public void listarRegistros(Entidad entidad){
        try{
            long longitud = fichero.length();
            if(longitud <=0){
                System.out.println("No hay Registros");
                return;
            }
            fichero.seek(0);
            byte[] tmpArrayByte;
            String linea = "";
            for(Atributos atributo : entidad.getAtributos()){
               linea += atributo.getNombre().toString().trim() + "\t\t";
            }
            System.out.println(linea);
            while(longitud>= entidad.getBytes()){
                linea = "";
                for(Atributos atributo : entidad.getAtributos()){
                    switch(atributo.getTipoDato()){
                        case INT:
                            int tmpInt = fichero.readInt();
                            linea += String.valueOf(tmpInt) + "\t\t";
                            break;
                        
                        case LONG:
                            long tmpLong = fichero.readLong();
                            linea += String.valueOf(tmpLong) + "\t\t";
                            break;
                            
                        case STRING:
                            tmpArrayByte = new byte[atributo.getLongitud()];
                            fichero.read(tmpArrayByte);
                            String tmpString = new String(tmpArrayByte);
                            linea += tmpString.trim() + "\t\t";
                            break;
                            
                        case DOUBLE:
                            double tmpDouble = fichero.readDouble();
                            linea += String.valueOf(tmpDouble)+ "\t\t";
                            break;
                        
                        case FLOAT:
                            float tmpFloat = fichero.readFloat();
                            linea += String.valueOf(tmpFloat)+ "\t\t";
                            break;
                        
                        case DATE:
                            tmpArrayByte = new byte[atributo.getBytes()];
                            fichero.read(tmpArrayByte);
                            tmpString = new String(tmpArrayByte);
                            linea += tmpString.trim() + "\t\t";
                        
                        case CHAR:
                            char tmpChar = (char) fichero.readByte();
                            linea += tmpChar + "\t\t";
                            break;
                    }
                }
                fichero.readByte();
                longitud-=entidad.getBytes();
                System.out.println(linea);
            } 
        }catch(Exception e){
            System.out.println("Error: "+ e.getMessage());
        }
    }
        
    public Date stringToDate(String strFecha){
        Date date = null;
        try{
            date = format.parse(strFecha);
        }catch(Exception e){
            date = null;
            System.out.println("Error en Fecha"+ e.getMessage());
        }
        return date;
    }
    
    public String dateToString(Date date){
        String strFecha;
        strFecha = format.format(date);
        return strFecha;
    }   
}
