/*
    classe Usuario
*/
export class Usuario {
    private id:number;
    private nome:string;
    private reputacao:number;
    private contato:string[12];
    private ajudante:boolean;

    constructor(id:number, nome:string,reputacao:number, contato:string[12], ajudante:boolean){
        this.id = id;
        this.nome = nome;
        this.reputacao = reputacao;
        this.contato = contato;
        this.ajudante = ajudante;
    }

    public getNome():string {
        return this.nome;
    }

    public getId():number{
        return this.id;
    }

    public getReputacao():number{
        return this.reputacao;
    }

    public getContato():string[12]{
        return this.contato;
    }

    public getAjudante():boolean{
        return this.ajudante;
    }

    private setNome(nome:string){
        this.nome = nome;
    }

    private setId(id:number){
        this.id = id;
    }

    private setReputacao(reputacao:number){
        this.reputacao = reputacao;
    }

    private setContato(contato:string[12]){
        this.contato = contato;
    }

    private setAjudante(ajudante:boolean){
        this.ajudante = ajudante;
    }
    
}
