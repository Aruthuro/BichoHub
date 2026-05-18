/*
    classe Usuario, considerando atualmente uma classe generica com nome e id sendo somente metodos get e set
*/

export class Usuario {
    private id:number;
    private nome:string;

    constructor(id:number, nome:string){
        this.id = id;
        this.nome = nome;
    }

    public getNome():string {
        return this.nome;
    }

    public getId():number{
        return this.id;
    }

    private setNome(nome:string){
        this.nome = nome;
    }

    private setId(id:number){
        this.id = id;
    }
    
}